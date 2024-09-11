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
function CronJobCard({ job, onUpdateJob }) {
  return (
    <Card className="w-full max-w-[400px] mb-4">
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
                value={job.cronExpression || ""}
                onChange={(e) => onUpdateJob(job.jobName, "cronExpression", e.target.value)}
                placeholder="Cron Expression"
              />
            </div>
            <div className="flex flex-col space-y-1.5">
              <Label htmlFor="fixedRate">Fixed Rate (ms)</Label>
              <Input
                id="fixedRate"
                value={job.fixedRate !== null ? job.fixedRate : ""}
                onChange={(e) => onUpdateJob(job.jobName, "fixedRate", e.target.value)}
                placeholder="Fixed Rate"
              />
            </div>
            <div className="flex flex-col space-y-1.5">
              <Label htmlFor="enabled">Enabled</Label>
              <Select
                value={job.enabled ? "true" : "false"}
                onValueChange={(value) => onUpdateJob(job.jobName, "enabled", value === "true")}
              >
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
        const response = await fetch("http://backend:8080/cron/configs")
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

  // Handle updating the value of an individual cron job field
  const updateJobField = (jobName: string, field: string, value: any) => {
    setCronJobs((prevJobs) =>
      prevJobs.map((job) =>
        job.jobName === jobName ? { ...job, [field]: value } : job
      )
    )
  }

  // Handle submitting all updates for the cron jobs
  const updateAllCronJobs = async () => {
    try {
      const response = await fetch(`http://backend:8080/cron/update`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(cronJobs), // Send the entire updated list
      })
      if (response.ok) {
        console.log("All cron jobs updated successfully")
      } else {
        console.error("Failed to update cron jobs")
      }
    } catch (error) {
      console.error("Error updating cron jobs:", error)
    }
  }

  if (loading) {
    return <div>Loading...</div>
  }

  return (
    <div className="container mx-auto px-4">
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        {cronJobs.length > 0 ? (
          cronJobs.map((job) => (
            <CronJobCard
              key={job.jobName}
              job={job}
              onUpdateJob={updateJobField} // Pass the field updater function
            />
          ))
        ) : (
          <div>No cron jobs found</div>
        )}
      </div>
      {/* Add a single update button at the bottom to update all jobs */}
      <div >
        <Button 
        
        variant='outline' 
        onClick={updateAllCronJobs}

        
        >Update All</Button>
      </div>
    </div>
  )
}
