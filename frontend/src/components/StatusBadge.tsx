"use client";

const STATUS_STYLES: Record<string, string> = {
  RECEIVED: "bg-amber-100 text-amber-800",
  CONFIRMED: "bg-blue-100 text-blue-800",
  DISPATCHED: "bg-indigo-100 text-indigo-800",
  DELIVERED: "bg-green-100 text-green-800",
  CANCELED: "bg-red-100 text-red-800",
};

const STATUS_LABELS: Record<string, string> = {
  RECEIVED: "Recebido",
  CONFIRMED: "Confirmado",
  DISPATCHED: "Despachado",
  DELIVERED: "Entregue",
  CANCELED: "Cancelado",
};

interface StatusBadgeProps {
  status: string;
}

export default function StatusBadge({ status }: StatusBadgeProps) {
  const style = STATUS_STYLES[status] || "bg-zinc-100 text-zinc-800";
  const label = STATUS_LABELS[status] || status;

  return (
    <span
      className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${style}`}
    >
      {label}
    </span>
  );
}
