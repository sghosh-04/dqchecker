import { LogOut, Moon, Sun } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/hooks/useAuth";
import { useTheme } from "@/hooks/useTheme";

export function Navbar() {
  const navigate = useNavigate();
  const { user, logout } = useAuth();
  const { theme, toggleTheme } = useTheme();

  return (
    <header className="sticky top-0 z-30 flex h-16 items-center justify-between border-b border-border bg-background/60 px-4 backdrop-blur-md md:px-6">
      <div className="flex items-center gap-3">
        <span className="inline-flex h-2 w-2 rounded-full bg-[#10a37f] animate-pulse" />
        <div>
          <p className="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">Session Profile</p>
          <p className="text-xs font-semibold text-foreground leading-tight mt-0.5">{user?.email}</p>
        </div>
      </div>
      <div className="flex items-center gap-2">
        <Button 
          variant="ghost" 
          size="icon" 
          onClick={toggleTheme} 
          className="h-8 w-8 hover:bg-neutral-200/50 dark:hover:bg-neutral-800/50 rounded-full shrink-0" 
          aria-label="Toggle theme"
        >
          {theme === "dark" ? <Sun className="h-4 w-4 text-amber-500" /> : <Moon className="h-4 w-4 text-neutral-600" />}
        </Button>
        <Button
          variant="outline"
          size="sm"
          onClick={() => {
            logout();
            navigate("/login");
          }}
          className="h-8 border-neutral-300 dark:border-neutral-700 hover:bg-neutral-200/40 dark:hover:bg-neutral-800/40 text-xs gap-1.5 shrink-0"
        >
          <LogOut className="h-3.5 w-3.5" />
          Logout
        </Button>
      </div>
    </header>
  );
}
