import { useEffect, useState } from 'react'
import { Layout } from '@/components/custom/layout'
import { Button } from '@/components/custom/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import ThemeSwitch from '@/components/theme-switch'
import { UserNav } from '@/components/user-nav'
import { Line, Bar } from 'react-chartjs-2'
import 'chart.js/auto'
import { MapContainer, TileLayer, Marker } from 'react-leaflet'
import 'leaflet/dist/leaflet.css'

export default function Dashboard() {
  const [beaconAnalytics, setBeaconAnalytics] = useState(null)
  const [cronJobAnalytics, setCronJobAnalytics] = useState(null)
  const [routeAnalytics, setRouteAnalytics] = useState(null)

  useEffect(() => {
    async function fetchBeaconAnalytics() {
      try {
        const response = await fetch('http://localhost:8080/api/analytics/beacons')
        const data = await response.json()
        setBeaconAnalytics(data)
      } catch (error) {
        console.error('Error fetching beacon analytics:', error)
      }
    }
    fetchBeaconAnalytics()
  }, [])

  useEffect(() => {
    async function fetchCronJobAnalytics() {
      try {
        const response = await fetch('http://localhost:8080/api/analytics/cron-jobs')
        const data = await response.json()
        setCronJobAnalytics(data)
      } catch (error) {
        console.error('Error fetching cron job analytics:', error)
      }
    }
    fetchCronJobAnalytics()
  }, [])

  useEffect(() => {
    async function fetchRouteAnalytics() {
      try {
        const response = await fetch('http://localhost:8080/api/analytics/routes')
        const data = await response.json()
        setRouteAnalytics(data)
      } catch (error) {
        console.error('Error fetching route analytics:', error)
      }
    }
    fetchRouteAnalytics()
  }, [])

  // Line chart data for beacon battery levels
  const beaconBatteryData = {
    labels: beaconAnalytics
      ? beaconAnalytics.latestBeacons.map((beacon) => beacon.name)
      : [],
    datasets: [
      {
        label: 'Battery Level (%)',
        data: beaconAnalytics
          ? beaconAnalytics.latestBeacons.map((beacon) => beacon.batteryLevel || 0)
          : [],
        fill: false,
        borderColor: '#4CAF50',
        tension: 0.1,
      },
    ],
  }

  // Bar chart comparing total beacons, cron jobs, and routes
  const comparisonData = {
    labels: ['Beacons', 'Cron Jobs', 'Routes'],
    datasets: [
      {
        label: 'Total Count',
        data: [
          beaconAnalytics ? beaconAnalytics.totalBeacons : 0,
          cronJobAnalytics ? cronJobAnalytics.totalCronJobsExecuted : 0,
          routeAnalytics ? routeAnalytics.totalRoutes : 0,
        ],
        backgroundColor: ['#FFC107', '#03A9F4', '#8BC34A'],
      },
    ],
  }

  // Map markers for routes
  const routeMarkers = routeAnalytics
    ? routeAnalytics.latestRoutes.map((route) => ({
        position: [route.latitude, route.longitude],
        name: route.beaconName,
      }))
    : []

  return (
    <Layout>
      <Layout.Header>
        <div className='ml-auto flex items-center space-x-4'>
          <ThemeSwitch />
          <UserNav />
        </div>
      </Layout.Header>

      <Layout.Body>
        <div className='mb-2 flex items-center justify-between space-y-2'>
          <h1 className='text-2xl font-bold tracking-tight'>Dashboard</h1>
        </div>

        <Tabs orientation='vertical' defaultValue='overview' className='space-y-4'>
          <div className='w-full overflow-x-auto pb-2'>
            <TabsList>
              <TabsTrigger value='overview'>Overview</TabsTrigger>
              <TabsTrigger value='analytics'>Analytics</TabsTrigger>
            </TabsList>
          </div>
          <TabsContent value='overview' className='space-y-4'>
            <div className='grid gap-4 sm:grid-cols-2 lg:grid-cols-4'>
              {/* Total Beacons */}
              <Card>
                <CardHeader>
                  <CardTitle>Total Beacons entries</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className='text-2xl font-bold'>
                    {beaconAnalytics ? beaconAnalytics.totalBeacons : 'Loading...'}
                  </div>
                </CardContent>
              </Card>

              {/* Total Cron Jobs */}
              <Card>
                <CardHeader>
                  <CardTitle>Total Cron Jobs Executed</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className='text-2xl font-bold'>
                    {cronJobAnalytics ? cronJobAnalytics.totalCronJobsExecuted : 'Loading...'}
                  </div>
                </CardContent>
              </Card>

              {/* Total Routes */}
              <Card>
                <CardHeader>
                  <CardTitle>Total Routes</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className='text-2xl font-bold'>
                    {routeAnalytics ? routeAnalytics.totalRoutes : 'Loading...'}
                  </div>
                </CardContent>
              </Card>

              {/* Bar chart for comparison */}
              <Card>
                <CardHeader>
                  <CardTitle>Comparison</CardTitle>
                </CardHeader>
                <CardContent>
                  <Bar data={comparisonData} />
                </CardContent>
              </Card>
            </div>

            {/* Line chart for beacon battery levels */}
            <Card>
              <CardHeader>
                <CardTitle>Beacon Battery Levels</CardTitle>
              </CardHeader>
              <CardContent>
                <Line data={beaconBatteryData} />
              </CardContent>
            </Card>

            {/* Failed Cron Jobs */}
            <Card>
              <CardHeader>
                <CardTitle>Failed Cron Jobs</CardTitle>
              </CardHeader>
              <CardContent>
                <table className='min-w-full table-auto'>
                  <thead>
                    <tr>
                      <th>Job Name</th>
                      <th>Start Time</th>
                      <th>Error</th>
                    </tr>
                  </thead>
                  <tbody>
                    {cronJobAnalytics &&
                      cronJobAnalytics.failedCronJobs.map((job) => (
                        <tr key={job.id}>
                          <td>{job.jobName}</td>
                          <td>{new Date(job.startTime).toLocaleString()}</td>
                          <td>{job.errorMessage}</td>
                        </tr>
                      ))}
                  </tbody>
                </table>
              </CardContent>
            </Card>

            {/* Map for routes
            <Card>
              <CardHeader>
                <CardTitle>Routes Map</CardTitle>
              </CardHeader>
              <CardContent>
                <MapContainer center={[49.6816, 12.2002]} zoom={13} style={{ height: '400px' }}>
                  <TileLayer
                    url='https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png'
                    attribution='&copy; OpenStreetMap contributors'
                  />
                  {routeMarkers.map((marker, index) => (
                    <Marker key={index} position={marker.position} />
                  ))}
                </MapContainer>
              </CardContent>
            </Card> */}
          </TabsContent>
        </Tabs>
      </Layout.Body>
    </Layout>
  )
}
