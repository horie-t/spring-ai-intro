"use client";

import { useState, useRef } from "react";
import { Mic, Square } from "lucide-react";
import { Button } from "@/components/ui/button";

interface VoiceInputProps {
  onVoiceRecorded: (audioBlob: Blob) => void;
  disabled?: boolean;
}

export function VoiceInput({ onVoiceRecorded, disabled = false }: VoiceInputProps) {
  const [isRecording, setIsRecording] = useState(false);
  const mediaRecorderRef = useRef<MediaRecorder | null>(null);
  const chunksRef = useRef<Blob[]>([]);

  const startRecording = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      const mediaRecorder = new MediaRecorder(stream);
      mediaRecorderRef.current = mediaRecorder;
      chunksRef.current = [];

      mediaRecorder.ondataavailable = (event) => {
        if (event.data.size > 0) {
          chunksRef.current.push(event.data);
        }
      };

      mediaRecorder.onstop = () => {
        const audioBlob = new Blob(chunksRef.current, { type: "audio/webm" });
        onVoiceRecorded(audioBlob);
        stream.getTracks().forEach((track) => track.stop());
      };

      mediaRecorder.start();
      setIsRecording(true);
    } catch (error) {
      console.error("Error accessing microphone:", error);
    }
  };

  const stopRecording = () => {
    if (mediaRecorderRef.current && isRecording) {
      mediaRecorderRef.current.stop();
      setIsRecording(false);
    }
  };

  return (
    <Button
      type="button"
      variant="ghost"
      size="icon"
      onClick={isRecording ? stopRecording : startRecording}
      disabled={disabled}
      className="h-[34px] w-[34px] rounded-full"
      aria-label={isRecording ? "Stop recording" : "Start voice input"}
    >
      {isRecording ? (
        <Square className="h-5 w-5 fill-current text-destructive" />
      ) : (
        <Mic className="h-5 w-5" />
      )}
    </Button>
  );
}
