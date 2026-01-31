"use client";

import { ThreadWithVoice } from '@/components/ThreadWithVoice';
import { ProtectedRoute } from '@/components/ProtectedRoute';

export default function Page() {
  return (
    <ProtectedRoute>
      <main className="h-screen flex items-center justify-center">
        <div className="w-full max-w-3xl h-[80vh] border rounded-xl p-4 overflow-hidden">
          <ThreadWithVoice />
        </div>
      </main>
    </ProtectedRoute>
  );
}
