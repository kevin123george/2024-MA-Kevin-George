import { ColumnDef } from '@tanstack/react-table'

import { Checkbox } from '@/components/ui/checkbox'
import { DataTableColumnHeader } from './data-table-column-header'
import { Task } from '../data/schema'

export const columns: ColumnDef<Task>[] = [
  {
    id: 'select',
    header: ({ table }) => (
      <Checkbox
        checked={
          table.getIsAllPageRowsSelected() ||
          (table.getIsSomePageRowsSelected() && 'indeterminate')
        }
        onCheckedChange={(value) => table.toggleAllPageRowsSelected(!!value)}
        aria-label='Select all'
        className='translate-y-[2px]'
      />
    ),
    cell: ({ row }) => (
      <Checkbox
        checked={row.getIsSelected()}
        onCheckedChange={(value) => row.toggleSelected(!!value)}
        aria-label='Select row'
        className='translate-y-[2px]'
      />
    ),
    enableSorting: false,
    enableHiding: false,
  },
  {
    accessorKey: 'id',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title='ID' />
    ),
    cell: ({ row }) => <div className='w-[80px]'>{row.getValue('id')}</div>,
    enableSorting: true,
    enableHiding: false,
  },
  {
    accessorKey: 'deviceid',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title='Device ID' />
    ),
    cell: ({ row }) => <div className='w-[120px]'>{row.getValue('deviceid')}</div>,
    enableSorting: true,
  },
  {
    accessorKey: 'devicename',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title='Device Name' />
    ),
    cell: ({ row }) => (
      <span className='max-w-32 truncate font-medium sm:max-w-72 md:max-w-[31rem]'>
        {row.getValue('devicename')}
      </span>
    ),
  },
  {
    accessorKey: 'latitude',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title='Latitude' />
    ),
    cell: ({ row }) => <div>{row.getValue('latitude')}</div>,
  },
  {
    accessorKey: 'longitude',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title='Longitude' />
    ),
    cell: ({ row }) => <div>{row.getValue('longitude')}</div>,
  },
  {
    accessorKey: 'protocol',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title='Protocol' />
    ),
    cell: ({ row }) => <div>{row.getValue('protocol')}</div>,
  },
  {
    accessorKey: 'devicetime',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title='Device Time' />
    ),
    cell: ({ row }) => <div>{row.getValue('devicetime')}</div>,
  },
]
