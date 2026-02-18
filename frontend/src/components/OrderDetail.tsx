"use client";

import { useEffect, useState, useCallback } from "react";
import { fetchOrderById } from "@/lib/api";
import { OrderWrapper } from "@/types/order";
import { formatCurrency, formatDate } from "@/lib/format";
import StatusBadge from "./StatusBadge";
import StatusTimeline from "./StatusTimeline";
import ActionButtons from "./ActionButtons";

interface OrderDetailProps {
  orderId: string;
}

export default function OrderDetail({ orderId }: OrderDetailProps) {
  const [data, setData] = useState<OrderWrapper | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadOrder = useCallback(() => {
    setLoading(true);
    fetchOrderById(orderId)
      .then(setData)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [orderId]);

  useEffect(() => {
    loadOrder();
  }, [loadOrder]);

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-zinc-300 border-t-zinc-900" />
      </div>
    );
  }

  if (error || !data) {
    return (
      <div className="rounded-lg border border-red-200 bg-red-50 p-4 text-red-800">
        {error || "Pedido não encontrado"}
      </div>
    );
  }

  const { order } = data;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h2 className="text-xl font-bold text-zinc-900 dark:text-zinc-50">
            Pedido #{orderId}
          </h2>
          <p className="text-sm text-zinc-500 dark:text-zinc-400">
            Criado em {formatDate(order.created_at)}
          </p>
        </div>
        <StatusBadge status={order.last_status_name} />
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        {/* Cliente */}
        <section className="rounded-lg border border-zinc-200 bg-white p-5 dark:border-zinc-800 dark:bg-zinc-900">
          <h3 className="mb-3 text-sm font-semibold uppercase tracking-wider text-zinc-500 dark:text-zinc-400">
            Cliente
          </h3>
          <dl className="space-y-2 text-sm">
            <div>
              <dt className="text-zinc-500 dark:text-zinc-400">Nome</dt>
              <dd className="font-medium text-zinc-900 dark:text-zinc-100">
                {order.customer.name}
              </dd>
            </div>
            <div>
              <dt className="text-zinc-500 dark:text-zinc-400">Telefone</dt>
              <dd className="font-medium text-zinc-900 dark:text-zinc-100">
                {order.customer.temporary_phone}
              </dd>
            </div>
          </dl>
        </section>

        {/* Endereco */}
        <section className="rounded-lg border border-zinc-200 bg-white p-5 dark:border-zinc-800 dark:bg-zinc-900">
          <h3 className="mb-3 text-sm font-semibold uppercase tracking-wider text-zinc-500 dark:text-zinc-400">
            Endereco de Entrega
          </h3>
          <dl className="space-y-2 text-sm">
            <div>
              <dd className="font-medium text-zinc-900 dark:text-zinc-100">
                {order.delivery_address.street_name},{" "}
                {order.delivery_address.street_number}
              </dd>
            </div>
            <div>
              <dd className="text-zinc-600 dark:text-zinc-400">
                {order.delivery_address.neighborhood} -{" "}
                {order.delivery_address.city}/{order.delivery_address.state}
              </dd>
            </div>
            <div>
              <dd className="text-zinc-600 dark:text-zinc-400">
                CEP: {order.delivery_address.postal_code}
              </dd>
            </div>
            {order.delivery_address.reference && (
              <div>
                <dd className="text-zinc-500 dark:text-zinc-400">
                  Ref: {order.delivery_address.reference}
                </dd>
              </div>
            )}
          </dl>
        </section>

        {/* Itens */}
        <section className="rounded-lg border border-zinc-200 bg-white p-5 dark:border-zinc-800 dark:bg-zinc-900 lg:col-span-2">
          <h3 className="mb-3 text-sm font-semibold uppercase tracking-wider text-zinc-500 dark:text-zinc-400">
            Itens do Pedido
          </h3>
          <div className="overflow-x-auto">
            <table className="min-w-full text-sm">
              <thead>
                <tr className="border-b border-zinc-200 dark:border-zinc-700">
                  <th className="pb-2 text-left font-medium text-zinc-500 dark:text-zinc-400">
                    Item
                  </th>
                  <th className="pb-2 text-center font-medium text-zinc-500 dark:text-zinc-400">
                    Qtd
                  </th>
                  <th className="pb-2 text-right font-medium text-zinc-500 dark:text-zinc-400">
                    Preço
                  </th>
                  <th className="pb-2 text-right font-medium text-zinc-500 dark:text-zinc-400">
                    Total
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-zinc-100 dark:divide-zinc-800">
                {order.items.map((item, idx) => (
                  <tr key={idx}>
                    <td className="py-2 text-zinc-900 dark:text-zinc-100">
                      <div>{item.name}</div>
                      {item.observations && (
                        <div className="text-xs text-zinc-500">
                          Obs: {item.observations}
                        </div>
                      )}
                    </td>
                    <td className="py-2 text-center text-zinc-600 dark:text-zinc-400">
                      {item.quantity}
                    </td>
                    <td className="py-2 text-right text-zinc-600 dark:text-zinc-400">
                      {formatCurrency(item.price)}
                    </td>
                    <td className="py-2 text-right font-medium text-zinc-900 dark:text-zinc-100">
                      {formatCurrency(item.total_price)}
                    </td>
                  </tr>
                ))}
              </tbody>
              <tfoot>
                <tr className="border-t border-zinc-200 dark:border-zinc-700">
                  <td
                    colSpan={3}
                    className="pt-2 text-right font-semibold text-zinc-900 dark:text-zinc-100">
                    Total:
                  </td>
                  <td className="pt-2 text-right font-bold text-zinc-900 dark:text-zinc-100">
                    {formatCurrency(order.total_price)}
                  </td>
                </tr>
              </tfoot>
            </table>
          </div>
        </section>

        {/* Pagamento */}
        <section className="rounded-lg border border-zinc-200 bg-white p-5 dark:border-zinc-800 dark:bg-zinc-900">
          <h3 className="mb-3 text-sm font-semibold uppercase tracking-wider text-zinc-500 dark:text-zinc-400">
            Pagamento
          </h3>
          {order.payments.map((payment, idx) => (
            <dl key={idx} className="space-y-2 text-sm">
              <div className="flex justify-between">
                <dt className="text-zinc-500 dark:text-zinc-400">Forma</dt>
                <dd className="font-medium text-zinc-900 dark:text-zinc-100">
                  {payment.origin.replace("_", " ")}
                </dd>
              </div>
              <div className="flex justify-between">
                <dt className="text-zinc-500 dark:text-zinc-400">Valor</dt>
                <dd className="font-medium text-zinc-900 dark:text-zinc-100">
                  {formatCurrency(payment.value)}
                </dd>
              </div>
              <div className="flex justify-between">
                <dt className="text-zinc-500 dark:text-zinc-400">Pre-pago</dt>
                <dd className="font-medium text-zinc-900 dark:text-zinc-100">
                  {payment.prepaid ? "Sim" : "Nao"}
                </dd>
              </div>
            </dl>
          ))}
        </section>

        {/* Histórico de Status */}
        <section className="rounded-lg border border-zinc-200 bg-white p-5 dark:border-zinc-800 dark:bg-zinc-900">
          <h3 className="mb-3 text-sm font-semibold uppercase tracking-wider text-zinc-500 dark:text-zinc-400">
            Histórico de Status
          </h3>
          <StatusTimeline statuses={order.statuses} />
        </section>

        {/* Acoes */}
        <section className="rounded-lg border border-zinc-200 bg-white p-5 dark:border-zinc-800 dark:bg-zinc-900 lg:col-span-2">
          <h3 className="mb-3 text-sm font-semibold uppercase tracking-wider text-zinc-500 dark:text-zinc-400">
            Ações
          </h3>
          <ActionButtons
            currentStatus={order.last_status_name}
            orderId={orderId}
            onStatusUpdated={loadOrder}
          />
        </section>
      </div>
    </div>
  );
}
