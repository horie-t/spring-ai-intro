package com.example.spring_ai_demo.adapter.in.filesystem;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.filters.ChainFileListFilter;
import org.springframework.integration.file.filters.FileSystemPersistentAcceptOnceFileListFilter;
import org.springframework.integration.file.filters.LastModifiedFileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.metadata.ConcurrentMetadataStore;
import org.springframework.integration.metadata.PropertiesPersistingMetadataStore;
import org.springframework.messaging.MessageChannel;

import java.io.File;
import java.time.Duration;

@Configuration
@EnableIntegration
public class LinuxFileWatcherConfig {

    // Linuxの絶対パス
    private final String INPUT_DIR = "/var/opt/smb";
    private final String METADATA_DIR = "/var/opt/.smb_metadata";

    @Bean
    public MessageChannel fileInputChannel() {
        return new DirectChannel();
    }

    // 1. メタデータストア（処理済みフラグの永続化）
    @Bean
    public ConcurrentMetadataStore metadataStore() {
        PropertiesPersistingMetadataStore store = new PropertiesPersistingMetadataStore();
        store.setBaseDirectory(METADATA_DIR);
        return store;
    }

    // 2. 更新検知 + 二度読み防止フィルタ
    @Bean
    public FileSystemPersistentAcceptOnceFileListFilter persistentFilter() {
        FileSystemPersistentAcceptOnceFileListFilter filter =
                new FileSystemPersistentAcceptOnceFileListFilter(metadataStore(), "linux-watcher-");
        filter.setFlushOnUpdate(true);
        return filter;
    }

    // 3. ファイル安定化フィルタ（Linuxでの書き込み中読み込み防止）
    // 最終更新から5秒以上経過しているファイルのみを対象とする
    @Bean
    public LastModifiedFileListFilter stabilityFilter() {
        LastModifiedFileListFilter filter = new LastModifiedFileListFilter();
        filter.setAge(Duration.ofSeconds(5));
        return filter;
    }

    @Bean
    @InboundChannelAdapter(value = "fileInputChannel", poller = @Poller(fixedDelay = "5000"))
    public MessageSource<File> fileReadingMessageSource() {
        FileReadingMessageSource source = new FileReadingMessageSource();
        source.setDirectory(new File(INPUT_DIR));

        // フィルタチェーン
        ChainFileListFilter<File> filterChain = new ChainFileListFilter<>();
        filterChain.addFilter(new SimplePatternFileListFilter("*.md")); // 特定の拡張子
        filterChain.addFilter(stabilityFilter()); // 書き込み完了待ち
        filterChain.addFilter(persistentFilter()); // 重複・更新検知

        source.setFilter(filterChain);
        return source;
    }

    @ServiceActivator(inputChannel = "fileInputChannel")
    public void handleFile(File file) {
        // Linuxの標準的な処理フロー
        System.out.println("Processing Linux file: " + file.getAbsolutePath());

//        // 処理が終わったら「archive」へ移動させるのがLinuxサーバでは一般的
//        File archivedFile = new File("/var/opt/archived-folder/" + file.getName());
//        if (file.renameTo(archivedFile)) {
//            System.out.println("Archived successfully.");
//        }
    }
}
