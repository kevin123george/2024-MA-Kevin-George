import * as React from "react"
import { useEffect, useState } from "react"
import { Button } from "@/components/ui/button"
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"

// Component to render each card
interface CronJob {
  jobName: string;
  cronExpression?: string;
  fixedRate?: number | null;
  enabled: boolean;
}

function CronJobCard({ job }: { job: CronJob }) {
  return (
    <Card className="w-full max-w[400px] mb-4">
      <CardHeader>
        <CardTitle>{job.jobName}</CardTitle>
        <CardDescription>
          Manage the schedule for {job.jobName}.
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form>
          <div className="grid w-full items-center gap-4">
            <div className="flex flex-col space-y-1.5">
              <Label htmlFor="name">Name</Label>
              <Input id="name" value={job.jobName} readOnly />
            </div>
            <div className="flex flex-col space-y-1.5">
              <Label htmlFor="cronExpression">Cron Expression</Label>
              <Input
                id="cronExpression"
                value={job.cronExpression || "N/A"}
                placeholder="Cron Expression"
                readOnly
              />
            </div>
            <div className="flex flex-col space-y-1.5">
              <Label htmlFor="fixedRate">Fixed Rate (ms)</Label>
              <Input
                id="fixedRate"
                value={job.fixedRate !== null ? job.fixedRate : "N/A"}
                placeholder="Fixed Rate"
                readOnly
              />
            </div>
            <div className="flex flex-col space-y-1.5">
              <Label htmlFor="enabled">Enabled</Label>
              <Select defaultValue={job.enabled ? "true" : "false"}>
                <SelectTrigger id="enabled">
                  <SelectValue placeholder="Select" />
                </SelectTrigger>
                <SelectContent position="popper">
                  <SelectItem value="true">True</SelectItem>
                  <SelectItem value="false">False</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
        </form>
      </CardContent>
      <CardFooter className="flex justify-between">
        <Button>Update</Button>
      </CardFooter>
    </Card>
  )
}

// Main component that fetches data and renders cards in a matrix/grid layout
export function CardWithForm() {
  interface CronJob {
    jobName: string;
    cronExpression?: string;
    fixedRate?: number | null;
    enabled: boolean;
  }

  const [cronJobs, setCronJobs] = useState<CronJob[]>([]) // State to store cron job data
  const [loading, setLoading] = useState(true) // State to handle loading

  // Fetch cron job configs on component mount
  useEffect(() => {
    async function fetchCronJobs() {
      try {
        const response = await fetch("http://localhost:8080/cron/configs")
        const data = await response.json()
        setCronJobs(data) // Store the fetched data in state
        setLoading(false)
      } catch (error) {
        console.error("Error fetching cron jobs:", error)
        setLoading(false)
      }
    }

    fetchCronJobs()
  }, [])

  if (loading) {
    return <div>Loading...</div>
  }

  return (
    <div className="container mx-auto px-4"> 
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        {cronJobs.length > 0 ? (
          cronJobs.map((job) => <CronJobCard key={job.jobName} job={job} />)
        ) : (
          <div>No cron jobs found</div>
        )}
      </div>
    </div>
  )
}
