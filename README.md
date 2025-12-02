# Spring AI入門

本リポジトリでは、[Spring AI](https://spring.io/projects/spring-ai)と[assistant-ui](https://www.assistant-ui.com/)を使ってチャットアプリケーションを実装しています。

アプリケーションの拡張の例として、
* [Brave Search](https://brave.com/ja/search/)で開発されている[MCP Server](https://github.com/brave/brave-search-mcp-server)
* [OpenWeatherMap](https://openweathermap.org/)のAPIを呼び出す自作のMCP Server
を追加しています。

## 使い方

## OpenWeatherMap MCPサーバのビルド

まず、MCPサーバのビルドを行います。

```bash
cd mcp_server
./mvnw clean package
```

## バックエンドの起動

以下のファイルのパスをMCPサーバのjarファイルのパスに置き換えてください。

```yaml:backend/src/main/resources/application.yaml
            openweathermap-client:
              command: java
              args: ["-jar", "/home/tetsuya/repo/spring-ai-intro/mcp_server/target/spring-ai-mcp-demo-0.0.1-SNAPSHOT.jar"]
              env:
                OPENWEATHERMAP_API_KEY: "${OPENWEATHERMAP_API_KEY}"
```

以下のコマンドを実行して application-local.yamlに、[OpenAI](https://openai.com/ja-JP/index/openai-api/)のAPIキー、[Brave Search](https://brave.com/ja/search/)のAPIキー、[OpenWeatherMap](https://openweathermap.org/)のAPIキーを設定してください。

```bash
cd backend
cp application-local.yaml.sample application-local.yaml
```

以下のコマンドを実行します。

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

## フロントエンドエンドの起動

以下のコマンドを実行します。

```bash
cd frontend
npm run dev
```

## コード解説

解説は『[Spring AI入門](https://zenn.dev/thorie/books/spring-ai-for-ai-engineering)』を参照してください。