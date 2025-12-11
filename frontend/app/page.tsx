"use client";

import { Thread } from '@/components/assistant-ui/thread';
import { ProtectedRoute } from '@/components/ProtectedRoute';
import {Button} from "@/components/ui/button";

export default function Page() {
    return (
        <ProtectedRoute>
            <main className="h-screen flex items-center justify-center">
                <div className="w-full max-w-3xl h-[80vh] border rounded-xl p-4 overflow-hidden">
                    <Thread />
                </div>
            </main>
        </ProtectedRoute>
    );
}
