import { useEffect, useState } from 'react'
import { Layout } from '@/components/custom/layout'
import ThemeSwitch from '@/components/theme-switch'
import { UserNav } from '@/components/user-nav'
import { DataTable } from './components/data-table'
import { columns } from './components/columns'
import axios from 'axios'
import { z } from 'zod'
import { positionSchema, Position } from './data/schema' // Adjust the import path based on your file structure
import { fetchPositions } from '@/service/axios-calls'
import App from './components/map-component-props'
import MapComponent from './components/map-component-props'

export default function Positions() {
  const [positions, setPositions] = useState<Position[]>([]) // State to hold tasks
  const [loading, setLoading] = useState(true) // Loading state
  const [error, setError] = useState<string | null>(null) // Error state
  const [refreshActive, setRefreshActive] = useState(false) // State to control auto-refresh

  // Function to fetch tasks data from the API
  const fetchTasks = async () => {
    try {
      // Fetching the data from API
      const response = await fetchPositions()

      // Validate the response data against the schema
      const validatedTasks = positionSchema.array().parse(response.data)

      // Set the validated tasks data in the state
      setPositions(validatedTasks)
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

  useEffect(() => {
    // Call fetchTasks initially
    fetchTasks()

    let intervalId: NodeJS.Timeout | null = null

    // Start the interval if refreshActive is true
    if (refreshActive) {
      intervalId = setInterval(fetchTasks, 5000) // Set interval to refresh data every 5 seconds
    }

    // Clear interval on component unmount or when refreshActive changes
    return () => {
      if (intervalId) clearInterval(intervalId)
    }
  }, [refreshActive]) // Dependency array includes refreshActive

  // Toggle auto-refresh
  const toggleRefresh = () => {
    setRefreshActive((prevState) => !prevState)
  }

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
              Here&apos;s is the latest location of devices!
            </p>
          </div>
          <button
            className={`px-4 py-2 rounded ${refreshActive ? 'bg-red-500' : 'bg-green-500'} text-white`}
            onClick={toggleRefresh}
          >
            {refreshActive ? 'Stop Auto-Refresh' : 'Start Auto-Refresh'}
          </button>
        </div>
        <div className='-mx-4 flex-1 overflow-auto px-4 py-1 lg:flex-row lg:space-x-12 lg:space-y-0'>
          {/* Pass the dynamically fetched tasks to DataTable */}
          <DataTable data={positions} columns={columns} />
        </div>
      </Layout.Body>


      <div style={{ marginTop: '20px' }}>
        <h3 className="text-xl font-bold mb-4">Map of Device Locations</h3>
        {/* Pass any necessary props to your MapComponent */}
        {/* <MapComponent  /> */}
      </div>

    </Layout>
  )
}



