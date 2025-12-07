"use client";

import type { ReactNode } from "react";
import {
    AssistantRuntimeProvider,
    useLocalRuntime,
    type ChatModelAdapter, SimpleImageAttachmentAdapter, CompositeAttachmentAdapter, SimpleTextAttachmentAdapter
} from "@assistant-ui/react";

/**
 * データを指定されたファイル名でダウンロードする関数
 * @param data - 保存したいデータを
 * @param filename - ダウンロードするファイルの名前（例: 'sample.text'）
 * @param type - MIMEタイプ（JSONファイルの場合は 'text/plain'）
 */
const downloadJsonFile = (data: any, filename: string, type: string = 'text/plain') => {
    const blob = new Blob([data], { type });
    const a = document.createElement('a');
    a.href = URL.createObjectURL(blob);
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(a.href);
};

const SpringAIModelAdapter: ChatModelAdapter = {
    async run({ messages, abortSignal }) {
        const result = await fetch("http://localhost:8080/api/chat", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            // forward the messages in the chat to the API
            body: JSON.stringify(
                messages.at(-1)
            ),
            // if the user hits the "cancel" button or escape keyboard key, cancel the request
            signal: abortSignal,
        });

        const runResult = await result.json();
        if (runResult.content.length > 1) {
            const attachment = runResult.content[1];
            if (attachment.type === "file") {
                downloadJsonFile(attachment.data, attachment.filename, attachment.mimeType);
            }
        }
        return {
            content: [
                {
                    type: "text",
                    text: runResult.content[0].text
                }
            ]
        }
    },
};

const compositeAdapter = new CompositeAttachmentAdapter([
    new SimpleImageAttachmentAdapter(),
    new SimpleTextAttachmentAdapter(),
    // Add more adapters as needed
]);

export function SpringAIRuntimeProvider({
                                      children,
                                  }: Readonly<{
    children: ReactNode;
}>) {
    const runtime = useLocalRuntime(SpringAIModelAdapter, {
        adapters: { attachments: compositeAdapter },
    });

    return (
        <AssistantRuntimeProvider runtime={runtime}>
            {children}
        </AssistantRuntimeProvider>
    );
}
