import { Upload, FileCode, CheckCircle } from "lucide-react";
import { useRef } from "react";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";

export function FileUploader({ file, onFileChange }: { file: File | null; onFileChange: (file: File | null) => void }) {
  const inputRef = useRef<HTMLInputElement | null>(null);

  return (
    <div
      className={cn(
        "flex min-h-60 flex-col items-center justify-center rounded-xl border border-dashed border-neutral-300 dark:border-neutral-800 bg-neutral-50/50 dark:bg-neutral-900/10 p-8 text-center transition-all duration-200",
        "hover:border-[#10a37f]/65 hover:bg-neutral-100/35 dark:hover:bg-neutral-900/30"
      )}
    >
      {file ? (
        <FileCode className="h-10 w-10 text-[#10a37f]" />
      ) : (
        <Upload className="h-10 w-10 text-neutral-400 dark:text-neutral-500" />
      )}
      <p className="mt-4 text-sm font-semibold tracking-tight">
        {file ? file.name : "Drop a CSV or Excel file here"}
      </p>
      <p className="mt-1.5 text-xs text-muted-foreground max-w-xs leading-relaxed">
        {file 
          ? `${(file.size / 1024).toFixed(1)} KB is ready to validate` 
          : "Spreadsheet columns are analyzed and processed entirely in memory."}
      </p>
      <input
        ref={inputRef}
        type="file"
        accept=".csv,text/csv,.xls,.xlsx,application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        className="hidden"
        onChange={(event) => onFileChange(event.target.files?.[0] ?? null)}
      />
      <div className="mt-5 flex gap-2">
        <Button 
          type="button" 
          onClick={() => inputRef.current?.click()}
          className="h-9 px-4 bg-[#10a37f] hover:bg-[#10a37f]/90 text-white font-medium text-xs rounded-lg shadow-sm"
        >
          Select File
        </Button>
        {file && (
          <Button 
            type="button" 
            variant="outline" 
            onClick={() => onFileChange(null)}
            className="h-9 px-4 border-neutral-300 dark:border-neutral-700 hover:bg-neutral-200/50 dark:hover:bg-neutral-800/40 font-medium text-xs rounded-lg"
          >
            Clear
          </Button>
        )}
      </div>
    </div>
  );
}
