"use client";

import { useState } from "react";
import { Thread } from "@/components/assistant-ui/thread";
import { useAssistantApi } from "@assistant-ui/react";
import { VoiceInput } from "@/components/VoiceInput";
import { sendVoiceToBackend } from "@/hooks/useVoiceInput";

export function ThreadWithVoice() {
  const api = useAssistantApi();
  const [isProcessing, setIsProcessing] = useState(false);

  const handleVoiceRecorded = async (audioBlob: Blob) => {
    setIsProcessing(true);
    try {
      const transcribedText = await sendVoiceToBackend(audioBlob);
      if (transcribedText) {
        api.composer().setText(transcribedText);
      }
    } catch (error) {
      console.error("Failed to transcribe audio:", error);
    } finally {
      setIsProcessing(false);
    }
  };

  return (
    <div className="relative h-full">
      <Thread />
      <div className="absolute bottom-20 left-4 z-10">
        <VoiceInput
          onVoiceRecorded={handleVoiceRecorded}
          disabled={isProcessing}
        />
      </div>
    </div>
  );
}
