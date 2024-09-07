import { useEffect, useState } from 'react'
import { Layout } from '@/components/custom/layout'
import ThemeSwitch from '@/components/theme-switch'
import { UserNav } from '@/components/user-nav'
import { DataTable } from './components/data-table'
import { columns } from './components/columns'
import axios from 'axios'
import { z } from 'zod'
import { taskSchema, Task } from './data/schema' // Adjust the import path based on your file structure

export default function Tasks() {
  const [tasks, setTasks] = useState<Task[]>([]) // State to hold tasks
  const [loading, setLoading] = useState(true) // Loading state
  const [error, setError] = useState<string | null>(null) // Error state

  useEffect(() => {
    // Function to fetch tasks data from the API
    const fetchTasks = async () => {
      try {
        // Fetching the data from API
        const response = await axios.get('http://localhost:8080/api/positions')

        // Validate the response data against the schema
        const validatedTasks = taskSchema.array().parse(response.data)

        // Set the validated tasks data in the state
        setTasks(validatedTasks)
        setLoading(false)
      } catch (err) {
        if (err instanceof z.ZodError) {
          // Handle Zod validation errors
          setError('Validation error: ' + err.errors.map(e => e.message).join(', '))
        } else if (axios.isAxiosError(err)) {
          // Handle Axios errors
          setError('Error fetching data: ' + err.message)
        } else {
          // Handle other types of errors
          setError('Unexpected error: ' + (err as Error).message)
        }
        setLoading(false)
      }
    }

    // Call the function to fetch tasks
    fetchTasks()
  }, [])

  // Render a loading or error message if applicable
  if (loading) {
    return <div>Loading...</div>
  }

  if (error) {
    return <div>Error: {error}</div>
  }

  // Render the component with fetched tasks data
  return (
    <Layout>
      {/* ===== Top Heading ===== */}
      <Layout.Header sticky>
        <div className='ml-auto flex items-center space-x-4'>
          <ThemeSwitch />
          <UserNav />
        </div>
      </Layout.Header>
      <Layout.Body>
        <div className='mb-2 flex items-center justify-between space-y-2'>
          <div>
            <h2 className='text-2xl font-bold tracking-tight'>Welcome back!</h2>
            <p className='text-muted-foreground'>
              Here&apos;s a list of your tasks for this month!
            </p>
          </div>
        </div>
        <div className='-mx-4 flex-1 overflow-auto px-4 py-1 lg:flex-row lg:space-x-12 lg:space-y-0'>
          {/* Pass the dynamically fetched tasks to DataTable */}
          <DataTable data={tasks} columns={columns} />
        </div>
      </Layout.Body>
    </Layout>
  )
}
