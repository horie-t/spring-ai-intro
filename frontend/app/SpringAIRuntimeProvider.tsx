"use client";

import type { ReactNode } from "react";
import {
    AssistantRuntimeProvider,
    useLocalRuntime,
    type ChatModelAdapter, SimpleImageAttachmentAdapter, CompositeAttachmentAdapter, SimpleTextAttachmentAdapter
} from "@assistant-ui/react";

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

        const data = await result.json();
        return {
            content: [
                {
                    type: "text",
                    text: data.text,
                },
            ],
        };
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
