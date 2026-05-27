import { Outlet } from "react-router-dom";

export function AuthLayout() {
  return (
    <div className="grid min-h-screen bg-background text-foreground lg:grid-cols-[0.95fr_1.05fr]">
      <section className="hidden border-r border-border bg-primary px-10 py-12 text-primary-foreground lg:flex lg:flex-col lg:justify-between">
        <div>
          <p className="text-2xl font-bold">DQ Checker</p>
          <p className="mt-2 max-w-md text-sm text-primary-foreground/80">
            Validate CSV data, track rule failures, and review reports from one focused analytics workspace.
          </p>
        </div>
        <div className="grid grid-cols-3 gap-3">
          {["JWT Auth", "Rule Reports", "Admin View"].map((item) => (
            <div key={item} className="rounded-lg border border-white/20 p-4 text-sm">
              {item}
            </div>
          ))}
        </div>
      </section>
      <main className="flex items-center justify-center p-6">
        <Outlet />
      </main>
    </div>
  );
}
