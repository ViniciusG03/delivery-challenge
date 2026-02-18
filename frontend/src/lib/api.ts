import { OrderWrapper } from "@/types/order";

const API_BASE = "/api";

export async function fetchOrders(): Promise<OrderWrapper[]> {
  const res = await fetch(`${API_BASE}/orders`);
  if (!res.ok) throw new Error("Erro ao buscar pedidos");
  return res.json();
}

export async function fetchOrderById(id: string): Promise<OrderWrapper> {
  const res = await fetch(`${API_BASE}/orders/${id}`);
  if (!res.ok) throw new Error("Erro ao buscar pedido");
  return res.json();
}

export async function updateOrderStatus(
  id: string,
  status: string
): Promise<OrderWrapper> {
  const res = await fetch(`${API_BASE}/orders/${id}/status`, {
    method: "PATCH",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ status }),
  });
  if (!res.ok) {
    const err = await res.json();
    throw new Error(err.message || "Erro ao atualizar status");
  }
  return res.json();
}

export async function createOrder(
  order: OrderWrapper
): Promise<OrderWrapper> {
  const res = await fetch(`${API_BASE}/orders`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(order),
  });
  if (!res.ok) throw new Error("Erro ao criar pedido");
  return res.json();
}

export async function deleteOrder(id: string): Promise<void> {
  const res = await fetch(`${API_BASE}/orders/${id}`, {
    method: "DELETE",
  });
  if (!res.ok) throw new Error("Erro ao remover pedido");
}
