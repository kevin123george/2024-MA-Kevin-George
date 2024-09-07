import {
  IconApps,
  IconBoxSeam,
  IconChecklist,
  IconLayoutDashboard,
  IconRouteAltLeft,
  IconSettings,
  IconTruck,
  IconCurrentLocation,
  IconDatabase,
  IconDeviceSdCard
} from '@tabler/icons-react'

export interface NavLink {
  title: string
  label?: string
  href: string
  icon: JSX.Element
}

export interface SideLink extends NavLink {
  sub?: NavLink[]
}

export const sidelinks: SideLink[] = [
  {
    title: 'Dashboard',
    label: '',
    href: '/',
    icon: <IconLayoutDashboard size={18} />,
  },
  {
    title: 'Tasks',
    label: '3',
    href: '/tasks',
    icon: <IconChecklist size={18} />,
  },
  {
    title: 'CattleDB',
    label: '',
    href: '/cattleDB',
    icon: <IconDatabase size={18} />,
    sub: [
      {
        title: 'Position',
        label: '',
        href: '/cattleDB',
        icon: <IconCurrentLocation size={18} />,
      },
      {
        title: 'Devices',
        label: '',
        href: '/devices',
        icon: <IconDeviceSdCard size={18} />,
      },
    ]
  },
  {
    title: 'Apps',
    label: '',
    href: '/apps',
    icon: <IconApps size={18} />,
  },
  {
    title: 'Requests',
    label: '10',
    href: '/requests',
    icon: <IconRouteAltLeft size={18} />,
    sub: [
      {
        title: 'Trucks',
        label: '9',
        href: '/trucks',
        icon: <IconTruck size={18} />,
      },
      {
        title: 'Cargos',
        label: '',
        href: '/cargos',
        icon: <IconBoxSeam size={18} />,
      },
    ],
  },
  {
    title: 'Settings',
    label: '',
    href: '/settings',
    icon: <IconSettings size={18} />,
  },
]
