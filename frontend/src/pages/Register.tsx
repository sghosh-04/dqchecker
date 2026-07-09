import { FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useToast } from "@/components/ui/toast";
import { getErrorMessage } from "@/services/api";
import { register } from "@/services/auth";

export function Register() {
  const navigate = useNavigate();
  const { toast } = useToast();
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setLoading(true);
    try {
      await register({ username, email, password, role: "USER" });
      toast({ title: "Account created", description: "You can now log in.", kind: "success" });
      navigate("/login");
    } catch (error) {
      toast({ title: "Registration failed", description: getErrorMessage(error), kind: "error" });
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
        <CardTitle className="text-xl font-bold text-center">Create your account</CardTitle>
        <CardDescription className="text-center text-xs text-muted-foreground">
          Sign up to validate datasets and review details.
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form className="space-y-4" onSubmit={handleSubmit}>
          <div className="space-y-1.5">
            <Label htmlFor="username">Username</Label>
            <Input 
              id="username" 
              placeholder="sayuri" 
              value={username} 
              onChange={(event) => setUsername(event.target.value)} 
              required 
              className="h-10 rounded-lg"
            />
          </div>
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
            <Label htmlFor="password">Password</Label>
            <Input 
              id="password" 
              type="password" 
              placeholder="••••••••" 
              minLength={8} 
              value={password} 
              onChange={(event) => setPassword(event.target.value)} 
              required 
              className="h-10 rounded-lg"
            />
          </div>
          <Button className="w-full h-10 bg-[#10a37f] hover:bg-[#10a37f]/90 text-white font-semibold rounded-lg text-sm mt-2 transition-all" disabled={loading}>
            {loading ? <Loader2 className="h-4 w-4 animate-spin shrink-0" /> : null}
            Sign Up
          </Button>
        </form>
        <p className="mt-6 text-center text-xs text-muted-foreground">
          Already have an account? <Link className="font-semibold text-[#10a37f] hover:underline" to="/login">Sign In</Link>
        </p>
      </CardContent>
    </Card>
  );
}
