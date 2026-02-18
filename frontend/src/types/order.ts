export interface OrderWrapper {
  store_id: string;
  order_id: string;
  order: Order;
}

export interface Order {
  payments: Payment[];
  last_status_name: string;
  store: Store;
  total_price: number;
  order_id: string;
  items: Item[];
  created_at: number;
  statuses: OrderStatusEntry[];
  customer: Customer;
  delivery_address: DeliveryAddress;
}

export interface Payment {
  prepaid: boolean;
  value: number;
  origin: string;
}

export interface Store {
  name: string;
  id: string;
}

export interface Item {
  code: number;
  price: number;
  observations: string | null;
  total_price: number;
  name: string;
  quantity: number;
  discount: number;
  condiments: unknown[];
}

export interface OrderStatusEntry {
  created_at: number;
  name: string;
  order_id: string;
  origin: string;
}

export interface Customer {
  temporary_phone: string;
  name: string;
}

export interface DeliveryAddress {
  reference: string;
  street_name: string;
  postal_code: string;
  country: string;
  city: string;
  neighborhood: string;
  street_number: string;
  state: string;
  coordinates: Coordinates;
}

export interface Coordinates {
  longitude: number;
  latitude: number;
  id: number;
}
