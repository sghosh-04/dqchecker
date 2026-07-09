import { useEffect, useState } from "react";
import { EmptyState } from "@/components/EmptyState";
import { Loading } from "@/components/Loading";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { getErrorMessage } from "@/services/api";
import { getUsers } from "@/services/report";
import type { AdminUser } from "@/types/report";

export function Admin() {
  const [users, setUsers] = useState<AdminUser[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    getUsers()
      .then(setUsers)
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <Loading label="Loading users registry" />;
  }

  if (error) {
    return <EmptyState title="Could not load users" description={error} />;
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight text-foreground">Admin Directory</h1>
        <p className="text-sm text-muted-foreground">Manage and view all registered platforms users and credentials.</p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Registered Accounts</CardTitle>
          <CardDescription>Security profiles and creation timestamps for active users.</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="border rounded-xl overflow-hidden bg-background">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead className="font-semibold text-foreground">User ID</TableHead>
                  <TableHead className="font-semibold text-foreground">Username</TableHead>
                  <TableHead className="font-semibold text-foreground">Email Address</TableHead>
                  <TableHead className="font-semibold text-foreground">Access Privilege</TableHead>
                  <TableHead className="font-semibold text-foreground">Date Joined</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {users.map((user) => (
                  <TableRow key={user.id}>
                    <TableCell className="font-medium text-foreground">#{user.id}</TableCell>
                    <TableCell className="font-medium text-indigo-600 dark:text-indigo-400">{user.username}</TableCell>
                    <TableCell>{user.email}</TableCell>
                    <TableCell>
                      <Badge variant={user.role === "ADMIN" ? "success" : "muted"}>
                        {user.role}
                      </Badge>
                    </TableCell>
                    <TableCell className="text-xs text-muted-foreground">
                      {new Date(user.createdAt).toLocaleString()}
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
