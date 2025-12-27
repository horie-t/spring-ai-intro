package com.example.spring_ai_demo.adapter.in.filesystem;

import com.example.spring_ai_demo.adapter.out.persistence.RagIngestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.time.Duration;
import java.util.List;

@Configuration
@EnableIntegration
public class LinuxFileWatcherConfig {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RagIngestionService ingestionService;

    private final String INPUT_DIR = "/var/opt/smb";
    private final String METADATA_DIR = "/var/opt/.smb_metadata";

    public LinuxFileWatcherConfig(RagIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @Bean
    public MessageChannel fileInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public ConcurrentMetadataStore metadataStore() {
        PropertiesPersistingMetadataStore store = new PropertiesPersistingMetadataStore();
        store.setBaseDirectory(METADATA_DIR);
        return store;
    }

    @Bean
    public FileSystemPersistentAcceptOnceFileListFilter persistentFilter() {
        FileSystemPersistentAcceptOnceFileListFilter filter =
                new FileSystemPersistentAcceptOnceFileListFilter(metadataStore(), "linux-watcher-");
        filter.setFlushOnUpdate(true);
        return filter;
    }

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

        ChainFileListFilter<File> filterChain = new ChainFileListFilter<>();
        filterChain.addFilter(new SimplePatternFileListFilter("*.md")); // 特定の拡張子
        filterChain.addFilter(stabilityFilter());
        filterChain.addFilter(persistentFilter());

        source.setFilter(filterChain);
        return source;
    }

    @ServiceActivator(inputChannel = "fileInputChannel")
    public void handleFileWithAcl(File file) {
        logger.info("Processing Linux file: {}", file.getAbsolutePath());
        ingestionService.ingestFile(file);
    }
}
