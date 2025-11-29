"use client";

import { Thread } from '@/components/assistant-ui/thread';

export default function Page() {
    return (
        <main className="h-screen flex items-center justify-center">
            <div className="w-full max-w-3xl h-[80vh] border rounded-xl p-4 overflow-hidden">
                <Thread />
            </div>
        </main>
    );
}
