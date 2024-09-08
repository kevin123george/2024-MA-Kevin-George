import { useState, useEffect } from 'react'
import {
  IconAdjustmentsHorizontal,
  IconSortAscendingLetters,
  IconSortDescendingLetters,
} from '@tabler/icons-react'
import { Layout } from '@/components/custom/layout'
import { Input } from '@/components/ui/input'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Separator } from '@/components/ui/separator'
import ThemeSwitch from '@/components/theme-switch'
import { UserNav } from '@/components/user-nav'
import { Button } from '@/components/custom/button'

const statusText = new Map<string, string>([
  ['all', 'All Devices'],
  ['online', 'Online'],
  ['offline', 'Offline'],
])

export default function Devices() {
  const [sort, setSort] = useState('ascending')
  const [statusFilter, setStatusFilter] = useState('all')
  const [searchTerm, setSearchTerm] = useState('')
  const [devices, setDevices] = useState([])

  // Fetch device data from API
  useEffect(() => {
    async function fetchDevices() {
      try {
        const response = await fetch('http://localhost:8080/api/devices')
        const data = await response.json()
        setDevices(data)
      } catch (error) {
        console.error('Error fetching devices:', error)
      }
    }

    fetchDevices()
  }, [])

  // Sorting and filtering the devices based on search, status, and sort
  const filteredDevices = devices
    .sort((a, b) =>
      sort === 'ascending'
        ? a.name.localeCompare(b.name)
        : b.name.localeCompare(a.name)
    )
    .filter((device) =>
      statusFilter === 'online'
        ? device.status === 'online'
        : statusFilter === 'offline'
        ? device.status === 'offline'
        : true
    )
    .filter((device) =>
      device.name.toLowerCase().includes(searchTerm.toLowerCase())
    )

  return (
    <Layout fixed>
      {/* ===== Top Heading ===== */}
      <Layout.Header>
        <div className='flex w-full items-center justify-between'>
          <div className='flex items-center space-x-4'>
            <ThemeSwitch />
            <UserNav />
          </div>
        </div>
      </Layout.Header>

      {/* ===== Content ===== */}
      <Layout.Body className='flex flex-col'>
        <div>
          <h1 className='text-2xl font-bold tracking-tight'>Device Integrations</h1>
          <p className='text-muted-foreground'>
            Here&apos;s a list of your devices!
          </p>
        </div>
        <div className='my-4 flex items-end justify-between sm:my-0 sm:items-center'>
          <div className='flex flex-col gap-4 sm:my-4 sm:flex-row'>
            <Input
              placeholder='Filter devices...'
              className='h-9 w-40 lg:w-[250px]'
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
            <Select value={statusFilter} onValueChange={setStatusFilter}>
              <SelectTrigger className='w-36'>
                <SelectValue>{statusText.get(statusFilter)}</SelectValue>
              </SelectTrigger>
              <SelectContent>
                <SelectItem value='all'>All Devices</SelectItem>
                <SelectItem value='online'>Online</SelectItem>
                <SelectItem value='offline'>Offline</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <Select value={sort} onValueChange={setSort}>
            <SelectTrigger className='w-16'>
              <SelectValue>
                <IconAdjustmentsHorizontal size={18} />
              </SelectValue>
            </SelectTrigger>
            <SelectContent align='end'>
              <SelectItem value='ascending'>
                <div className='flex items-center gap-4'>
                  <IconSortAscendingLetters size={16} />
                  <span>Ascending</span>
                </div>
              </SelectItem>
              <SelectItem value='descending'>
                <div className='flex items-center gap-4'>
                  <IconSortDescendingLetters size={16} />
                  <span>Descending</span>
                </div>
              </SelectItem>
            </SelectContent>
          </Select>
        </div>
        <Separator className='shadow' />
        <ul className='faded-bottom no-scrollbar grid gap-4 overflow-auto pb-16 pt-4 md:grid-cols-2 lg:grid-cols-3'>
          {filteredDevices.map((device) => (
            <li
              key={device.uniqueId}
              className='rounded-lg border p-4 hover:shadow-md'
            >
              <div className='mb-4 flex items-start justify-between'>
                <div className='flex flex-col'>
                  <h2 className='text-lg font-semibold'>{device.name}</h2> {/* Adjusted font size */}
                  <span className='text-sm text-muted-foreground'>{device.uniqueId}</span>
                </div>
                <Button
                  variant='outline'
                  size='sm'
                  className={`${
                    device.status === 'online'
                      ? 'border border-green-300 bg-green-50 hover:bg-green-100 dark:border-green-700 dark:bg-green-950 dark:hover:bg-green-900'
                      : 'border border-gray-300 bg-gray-50 hover:bg-gray-100 dark:border-gray-700 dark:bg-gray-950 dark:hover:bg-gray-900'
                  }`}
                >
                  {device.status === 'online' ? 'Online' : 'Offline'}
                </Button>
              </div>
              <div>
                <h3 className='mb-1 text-sm font-semibold'>Model: {device.model || 'Unknown'}</h3>
                <p className='text-sm text-gray-500'>Battery: {device.batteryLevel}%</p>
                <p className='text-sm text-gray-500'>
                  Last Update: {new Date(device.lastUpdate).toLocaleString()}
                </p>
                <p className='text-sm text-gray-500'>
                  Firmware: {device.firmwareVersion || 'N/A'}
                </p>
              </div>
            </li>
          ))}
        </ul>
      </Layout.Body>
    </Layout>
  )
}
