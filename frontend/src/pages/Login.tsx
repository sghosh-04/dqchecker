import { FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useToast } from "@/components/ui/toast";
import { useAuth } from "@/hooks/useAuth";
import { getErrorMessage } from "@/services/api";
import { login } from "@/services/auth";

export function Login() {
  const navigate = useNavigate();
  const { setAuthenticated } = useAuth();
  const { toast } = useToast();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setLoading(true);
    try {
      await login({ email, password });
      setAuthenticated();
      toast({ title: "Welcome back", kind: "success" });
      navigate("/");
    } catch (error) {
      toast({ title: "Login failed", description: getErrorMessage(error), kind: "error" });
    } finally {
      setLoading(false);
    }
  }

  return (
    <Card className="w-full max-w-md border border-neutral-200 dark:border-neutral-800 shadow-sm rounded-2xl">
      <CardHeader className="space-y-1.5 pb-6">
        <div className="flex justify-center mb-4">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-[#10a37f] text-white font-bold text-base shadow-sm">
            DQ
          </div>
        </div>
        <CardTitle className="text-xl font-bold text-center">Welcome back</CardTitle>
        <CardDescription className="text-center text-xs text-muted-foreground">
          Enter your credentials to access your quality checks.
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form className="space-y-4" onSubmit={handleSubmit}>
          <div className="space-y-1.5">
            <Label htmlFor="email">Email address</Label>
            <Input 
              id="email" 
              type="email" 
              placeholder="name@example.com" 
              value={email} 
              onChange={(event) => setEmail(event.target.value)} 
              required 
              className="h-10 rounded-lg"
            />
          </div>
          <div className="space-y-1.5">
            <div className="flex justify-between items-center">
              <Label htmlFor="password">Password</Label>
            </div>
            <Input 
              id="password" 
              type="password" 
              placeholder="••••••••" 
              value={password} 
              onChange={(event) => setPassword(event.target.value)} 
              required 
              className="h-10 rounded-lg"
            />
          </div>
          <Button className="w-full h-10 bg-[#10a37f] hover:bg-[#10a37f]/90 text-white font-semibold rounded-lg text-sm mt-2 transition-all" disabled={loading}>
            {loading ? <Loader2 className="h-4 w-4 animate-spin shrink-0" /> : null}
            Sign In
          </Button>
        </form>
        <p className="mt-6 text-center text-xs text-muted-foreground">
          New to the platform? <Link className="font-semibold text-[#10a37f] hover:underline" to="/register">Create an account</Link>
        </p>
      </CardContent>
    </Card>
  );
}
