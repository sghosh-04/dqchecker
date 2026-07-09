import { BarChart3, FileUp, Shield, ClipboardList } from "lucide-react";
import { NavLink } from "react-router-dom";
import { cn } from "@/lib/utils";
import { useAuth } from "@/hooks/useAuth";

const items = [
  { to: "/", label: "Dashboard", icon: BarChart3 },
  { to: "/upload", label: "Upload & Validate", icon: FileUp },
  { to: "/reports", label: "Report History", icon: ClipboardList }
];

export function Sidebar() {
  const { user } = useAuth();

  return (
    <aside className="hidden w-64 border-r border-border bg-[#f9f9f9] dark:bg-[#171717] md:block">
      <div className="flex h-16 items-center px-6">
        <div className="flex items-center gap-2">
          <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-[#10a37f] text-white font-bold text-sm select-none shadow-sm">
            DQ
          </div>
          <div>
            <p className="text-sm font-semibold tracking-tight text-foreground leading-none">DQ Checker</p>
            <p className="text-[9px] text-muted-foreground uppercase font-bold tracking-wider mt-1">Quality Platform</p>
          </div>
        </div>
      </div>
      <div className="px-3 py-2">
        <nav className="space-y-1">
          {items.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                cn(
                  "flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium text-muted-foreground transition-all duration-150 hover:bg-neutral-200/50 dark:hover:bg-neutral-800/40 hover:text-foreground",
                  isActive && "bg-neutral-200/80 dark:bg-neutral-800/70 text-foreground font-semibold"
                )
              }
            >
              <item.icon className="h-4 w-4 shrink-0" />
              {item.label}
            </NavLink>
          ))}
          {user?.role === "ADMIN" && (
            <NavLink
              to="/admin"
              className={({ isActive }) =>
                cn(
                  "flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium text-muted-foreground transition-all duration-150 hover:bg-neutral-200/50 dark:hover:bg-neutral-800/40 hover:text-foreground",
                  isActive && "bg-neutral-200/80 dark:bg-neutral-800/70 text-foreground font-semibold"
                )
              }
            >
              <Shield className="h-4 w-4 shrink-0" />
              Admin Console
            </NavLink>
          )}
        </nav>
      </div>
    </aside>
  );
}
