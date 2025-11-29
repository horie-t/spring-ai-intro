"use client";

import type { ReactNode } from "react";
import {
    AssistantRuntimeProvider,
    useLocalRuntime,
    type ChatModelAdapter,
} from "@assistant-ui/react";

const SpringAIModelAdapter: ChatModelAdapter = {
    async run({ messages, abortSignal }) {
        const result = await fetch("http://localhost:8080", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            // forward the messages in the chat to the API
            body: JSON.stringify({
                messages,
            }),
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

export function SpringAIRuntimeProvider({
                                      children,
                                  }: Readonly<{
    children: ReactNode;
}>) {
    const runtime = useLocalRuntime(SpringAIModelAdapter);

    return (
        <AssistantRuntimeProvider runtime={runtime}>
            {children}
        </AssistantRuntimeProvider>
    );
}
