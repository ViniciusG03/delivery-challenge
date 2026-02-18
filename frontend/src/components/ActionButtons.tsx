"use client";

import { useState } from "react";
import { updateOrderStatus } from "@/lib/api";

const VALID_TRANSITIONS: Record<string, string[]> = {
  RECEIVED: ["CONFIRMED", "CANCELED"],
  CONFIRMED: ["DISPATCHED", "CANCELED"],
  DISPATCHED: ["DELIVERED", "CANCELED"],
  DELIVERED: [],
  CANCELED: [],
};

const ACTION_LABELS: Record<string, string> = {
  CONFIRMED: "Confirmar",
  DISPATCHED: "Despachar",
  DELIVERED: "Marcar Entregue",
  CANCELED: "Cancelar",
};

const ACTION_STYLES: Record<string, string> = {
  CONFIRMED: "bg-blue-600 hover:bg-blue-700 text-white",
  DISPATCHED: "bg-indigo-600 hover:bg-indigo-700 text-white",
  DELIVERED: "bg-green-600 hover:bg-green-700 text-white",
  CANCELED: "bg-red-600 hover:bg-red-700 text-white",
};

interface ActionButtonsProps {
  currentStatus: string;
  orderId: string;
  onStatusUpdated: () => void;
}

export default function ActionButtons({
  currentStatus,
  orderId,
  onStatusUpdated,
}: ActionButtonsProps) {
  const [loading, setLoading] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const transitions = VALID_TRANSITIONS[currentStatus] || [];

  if (transitions.length === 0) {
    return (
      <p className="text-sm text-zinc-500 dark:text-zinc-400">
        Este pedido está em estado final. Nenhuma ação disponível.
      </p>
    );
  }

  async function handleAction(targetStatus: string) {
    if (targetStatus === "CANCELED") {
      const confirmed = window.confirm(
        "Tem certeza que deseja cancelar este pedido?",
      );
      if (!confirmed) return;
    }

    setLoading(targetStatus);
    setError(null);

    try {
      await updateOrderStatus(orderId, targetStatus);
      onStatusUpdated();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Erro ao atualizar status");
    } finally {
      setLoading(null);
    }
  }

  return (
    <div className="space-y-3">
      <div className="flex flex-wrap gap-2">
        {transitions.map((status) => (
          <button
            key={status}
            onClick={() => handleAction(status)}
            disabled={loading !== null}
            className={`rounded-lg px-4 py-2 text-sm font-medium transition-colors disabled:opacity-50 ${ACTION_STYLES[status]}`}>
            {loading === status ? "Processando..." : ACTION_LABELS[status]}
          </button>
        ))}
      </div>
      {error && <p className="text-sm text-red-600">{error}</p>}
    </div>
  );
}
