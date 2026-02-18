"use client";

import { OrderStatusEntry } from "@/types/order";
import { formatDate } from "@/lib/format";
import StatusBadge from "./StatusBadge";

interface StatusTimelineProps {
  statuses: OrderStatusEntry[];
}

export default function StatusTimeline({ statuses }: StatusTimelineProps) {
  const sorted = [...statuses].sort((a, b) => a.created_at - b.created_at);

  return (
    <div className="flow-root">
      <ul className="-mb-8">
        {sorted.map((status, idx) => (
          <li key={idx}>
            <div className="relative pb-8">
              {idx < sorted.length - 1 && (
                <span
                  className="absolute left-3 top-6 -ml-px h-full w-0.5 bg-zinc-200 dark:bg-zinc-700"
                  aria-hidden="true"
                />
              )}
              <div className="relative flex items-start gap-3">
                <div className="flex h-6 w-6 items-center justify-center">
                  <div
                    className={`h-2.5 w-2.5 rounded-full ${
                      idx === sorted.length - 1
                        ? "bg-blue-600 ring-4 ring-blue-100 dark:ring-blue-900"
                        : "bg-zinc-400"
                    }`}
                  />
                </div>
                <div className="flex min-w-0 flex-1 items-center justify-between gap-4">
                  <div className="flex items-center gap-2">
                    <StatusBadge status={status.name} />
                    <span className="text-xs text-zinc-400">
                      via {status.origin}
                    </span>
                  </div>
                  <span className="whitespace-nowrap text-xs text-zinc-500 dark:text-zinc-400">
                    {formatDate(status.created_at)}
                  </span>
                </div>
              </div>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}
