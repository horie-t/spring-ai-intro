"use client";

export async function sendVoiceToBackend(audioBlob: Blob): Promise<string> {
  const formData = new FormData();
  formData.append("audio", audioBlob, "voice.webm");

  const response = await fetch("http://localhost:8080/api/voice/transcribe", {
    method: "POST",
    body: formData,
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error("Failed to send voice to backend");
  }

  const data = await response.json();
  return data.text || "";
}
