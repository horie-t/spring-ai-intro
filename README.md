# Spring AI入門

『[Spring AI入門](https://zenn.dev/thorie/books/spring-ai-for-ai-engineering)』を書くため検証コードのリポジトリです。

## 使い方

## MCPサーバのビルド

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
