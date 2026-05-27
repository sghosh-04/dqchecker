# DQ Checker Frontend

React + Vite + TypeScript dashboard for the `dqchecker` Spring Boot API.

## Stack

- React
- Vite
- TypeScript
- TailwindCSS
- React Router
- Axios
- Recharts
- shadcn/ui-style local components

## Setup

```bash
cd frontend
npm install
npm run dev
```

The app runs on `http://localhost:5173`.

## Environment

`.env`:

```env
VITE_API_URL=http://localhost:8080
```

## Build

```bash
npm run build
```

## Pages

- `/login`
- `/register`
- `/`
- `/upload`
- `/reports`
- `/reports/:id`
- `/admin`

## Notes

The JWT is stored in `localStorage` and attached to API calls through the Axios interceptor in `src/services/api.ts`.
