



import axios from 'axios'
import { z } from 'zod'
import { taskSchema, Task } from '../data/schema' // Import your schema

// Function to fetch and validate data
const fetchDataAndAssign = async () => {
  try {
    // Fetch data from the API
    const response = await axios.get('http://localhost:8080/api/positions/177657227')

    // Validate the data using Zod schema (assuming response is an array of tasks)
    const validatedData: Task[] = taskSchema.array().parse(response.data)

    // Assign the validated data to a variable
    const tasks: Task[] = validatedData

    console.log('Validated tasks:', tasks) // You can now work with the validated data

    return tasks // You can return this or do further processing
  } catch (err) {
    if (err instanceof z.ZodError) {
      // Handle validation errors
      console.error('Validation error:', err.errors)
    } else if (axios.isAxiosError(err)) {
      // Handle Axios-specific errors
      console.error('Error fetching data:', err.message)
    } else {
      // Handle any other errors
      console.error('Unexpected error:', (err as Error).message)
    }
  }
}

// Call the function to fetch and validate data
export const tasks = fetchDataAndAssign();


export const tassdsdks = [
  {
      "deviceid": 177657227,
      "longitude": 12.200226187705992,
      "protocol": "mqtt",
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "latitude": 49.681643848588095,
      "id": 49,
      "devicetime": "2024-07-30T12:31:30.644Z"
  },
  {
      "deviceid": 177657227,
      "longitude": 12.200226187705992,
      "protocol": "mqtt",
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "latitude": 49.681643848588095,
      "id": 81,
      "devicetime": "2024-07-30T12:31:30.644Z"
  },
  {
      "deviceid": 177657227,
      "id": 184,
      "longitude": 12.200226187705992,
      "protocol": "mqtt",
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "latitude": 49.681643848588095,
      "devicetime": "2024-07-30T12:31:30.644Z"
  },
  {
      "deviceid": 177657227,
      "longitude": 12.200226187705992,
      "protocol": "mqtt",
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 259,
      "latitude": 49.681643848588095,
      "devicetime": "2024-07-30T12:31:30.644Z"
  },
  {
      "deviceid": 177657227,
      "longitude": 12.200226187705992,
      "protocol": "mqtt",
      "id": 317,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "latitude": 49.681643848588095,
      "devicetime": "2024-07-30T12:31:30.644Z"
  },
  {
      "deviceid": 177657227,
      "id": 345,
      "longitude": 12.200226187705992,
      "protocol": "mqtt",
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "latitude": 49.681643848588095,
      "devicetime": "2024-07-30T12:31:30.644Z"
  },
  {
      "deviceid": 177657227,
      "id": 426,
      "longitude": 12.200226187705992,
      "protocol": "mqtt",
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "latitude": 49.681643848588095,
      "devicetime": "2024-07-30T12:31:30.644Z"
  },
  {
      "deviceid": 177657227,
      "id": 488,
      "longitude": 12.200226187705992,
      "protocol": "mqtt",
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "latitude": 49.681643848588095,
      "devicetime": "2024-07-30T12:31:30.644Z"
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T09:35:08.106Z",
      "longitude": 12.200226187705992,
      "protocol": "mqtt",
      "id": 573,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "latitude": 49.681643848588095
  },
  {
      "deviceid": 177657227,
      "id": 648,
      "longitude": 12.200226187705992,
      "protocol": "mqtt",
      "devicetime": "2024-09-05T09:37:42.901Z",
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "latitude": 49.681643848588095
  },
  {
      "deviceid": 177657227,
      "longitude": 12.200226187705992,
      "protocol": "mqtt",
      "id": 684,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "latitude": 49.681643848588095,
      "devicetime": "2024-09-05T10:22:33.116Z"
  },
  {
      "deviceid": 177657227,
      "id": 778,
      "longitude": 12.200226187705992,
      "protocol": "mqtt",
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "latitude": 49.681643848588095,
      "devicetime": "2024-09-05T10:22:33.116Z"
  },
  {
      "deviceid": 177657227,
      "longitude": 12.200226187705992,
      "protocol": "mqtt",
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "latitude": 49.681643848588095,
      "id": 785,
      "devicetime": "2024-09-05T10:48:26.082Z"
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T10:58:58.661Z",
      "longitude": 12.200226187705992,
      "protocol": "mqtt",
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "latitude": 49.681643848588095,
      "id": 854
  },
  {
      "deviceid": 177657227,
      "id": 969,
      "longitude": 12.200226187705992,
      "protocol": "mqtt",
      "devicetime": "2024-09-05T11:08:39.813Z",
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "latitude": 49.681643848588095
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T11:11:50.626Z",
      "longitude": 12.200226187705992,
      "protocol": "mqtt",
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "latitude": 49.681643848588095,
      "id": 1009
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T11:11:50.626Z",
      "longitude": 12.200226187705992,
      "protocol": "mqtt",
      "id": 1068,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "latitude": 49.681643848588095
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T11:11:50.626Z",
      "longitude": 12.200226187705992,
      "protocol": "mqtt",
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 1138,
      "latitude": 49.681643848588095
  },
  {
      "deviceid": 177657227,
      "id": 1178,
      "devicetime": "2024-09-05T11:11:50.626Z",
      "longitude": 12.200226187705992,
      "protocol": "mqtt",
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "latitude": 49.681643848588095
  },
  {
      "deviceid": 177657227,
      "id": 1273,
      "devicetime": "2024-09-05T11:11:50.626Z",
      "longitude": 12.200226187705992,
      "protocol": "mqtt",
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "latitude": 49.681643848588095
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T11:11:50.626Z",
      "longitude": 12.200226187705992,
      "protocol": "mqtt",
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "latitude": 49.681643848588095,
      "id": 1318
  },
  {
      "deviceid": 177657227,
      "latitude": 49.89764834241015,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 1411,
      "longitude": 10.878038434420885,
      "protocol": "osmand",
      "devicetime": "2024-09-05T12:19:44.578Z"
  },
  {
      "deviceid": 177657227,
      "latitude": 49.89764834241015,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "devicetime": "2024-09-05T12:27:04.308Z",
      "longitude": 10.878038434420885,
      "id": 1478,
      "protocol": "osmand"
  },
  {
      "deviceid": 177657227,
      "latitude": 49.89764834241015,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 1539,
      "devicetime": "2024-09-05T12:27:04.308Z",
      "longitude": 10.878038434420885,
      "protocol": "osmand"
  },
  {
      "deviceid": 177657227,
      "latitude": 49.89764834241015,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "devicetime": "2024-09-05T12:27:04.308Z",
      "longitude": 10.878038434420885,
      "id": 1574,
      "protocol": "osmand"
  },
  {
      "deviceid": 177657227,
      "latitude": 49.89764834241015,
      "id": 1628,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "devicetime": "2024-09-05T12:27:04.308Z",
      "longitude": 10.878038434420885,
      "protocol": "osmand"
  },
  {
      "devicetime": "2024-09-05T12:20:54.838Z",
      "deviceid": 177657227,
      "latitude": 49.89764834241015,
      "id": 1711,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 10.878038434420885,
      "protocol": "osmand"
  },
  {
      "devicetime": "2024-09-05T12:20:54.838Z",
      "deviceid": 177657227,
      "latitude": 49.89764834241015,
      "id": 1774,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 10.878038434420885,
      "protocol": "osmand"
  },
  {
      "deviceid": 177657227,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 1827,
      "devicetime": "2024-09-05T13:24:01.793Z",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 1897,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "devicetime": "2024-09-05T13:24:01.793Z",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "protocol": "mqtt",
      "id": 1996,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "devicetime": "2024-09-05T13:24:01.793Z",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "devicetime": "2024-09-05T13:24:01.793Z",
      "id": 2021,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "devicetime": "2024-09-05T14:01:46.926Z",
      "id": 2084,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 2186,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 2211,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 2289,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 2396,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 2421,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 2503,
      "longitude": 0.0
  },
  {
      "id": 2555,
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 2662,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 2703,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 2752,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 2830,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 2896,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 2956,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 3052,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 3114,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "id": 3131,
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 3237,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 3266,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 3335,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 3425,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 3457,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 3511,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 3629,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 3648,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 3736,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 3823,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 3859,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 3916,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 4026,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 4083,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 4141,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 4213,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 4236,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 4312,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 4376,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 4462,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 4489,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 4561,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 4657,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 4688,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 4767,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 4869,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 4879,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 4997,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 5040,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 5116,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 5199,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "id": 5259,
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 5277,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 5350,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 5429,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 5503,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 5555,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 5645,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 5712,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 5753,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 5848,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 5891,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 5969,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 6042,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 6099,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 6129,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 6215,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 6272,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 6344,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 6409,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 6454,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 6526,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 6581,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 6637,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 6751,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 6805,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 6829,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 6929,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 7013,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 7082,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "id": 7099,
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 7203,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 7271,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 7334,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 7367,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 7423,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 7498,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 7578,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 7665,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 7713,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 7758,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 7858,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 7872,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 7946,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 8006,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 8100,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 8172,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 8201,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 8275,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 8345,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "id": 8395,
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 8455,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 8544,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 8629,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 8674,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 8759,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 8815,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 8903,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 8947,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 9061,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 9129,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 9170,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 9242,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 9328,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 9357,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 9422,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 9489,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 9602,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 9644,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 9688,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "id": 9771,
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 9864,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 9912,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 9951,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 10051,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 10121,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 10191,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 10231,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 10276,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 10346,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 10432,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 10465,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 10549,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 10645,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 10669,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 10732,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 10803,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 10850,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 10930,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 11025,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 11091,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 11110,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 11213,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 11265,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 11338,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 11418,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 11490,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 11533,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 11585,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 11650,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 11709,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 11774,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 11860,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 11909,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 11987,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 12028,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 12109,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 12163,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 12224,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 12277,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 12378,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 12417,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 12505,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 12591,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 12634,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 12724,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 12733,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 12819,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 12886,
      "longitude": 0.0
  },
  {
      "id": 12939,
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 13013,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 13100,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 13129,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 13206,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 13287,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 13373,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 13420,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 13477,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 18007,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 13530,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 13635,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 13694,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 13728,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 13792,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 13885,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 13962,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "id": 14011,
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 14066,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 14115,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 14173,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 14241,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 14350,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 14374,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 14476,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 14534,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 14552,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 14647,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 14732,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 14767,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 14830,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 14932,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 14940,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 15039,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 15087,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 15153,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 15257,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 15278,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 15350,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 15418,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 15482,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 15550,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 15649,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 15674,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 15747,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 15798,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 15911,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 15949,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 15984,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 16099,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 16145,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 16213,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 16261,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 16325,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 16430,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 16485,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 16548,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 16599,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 16682,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 16736,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 16766,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "id": 16859,
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 16918,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 17018,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 17028,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 17106,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 17169,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 17223,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 17293,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 17377,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 17426,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 17539,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 17568,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 17614,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "id": 17723,
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 17770,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 17845,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 17928,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 17962,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 18112,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 18149,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 18250,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 18258,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "id": 18349,
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 18430,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 18485,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 18553,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 18632,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 18662,
      "longitude": 0.0
  },
  {
      "id": 18731,
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 18815,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 18857,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 18914,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 18983,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 19072,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 19135,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 19171,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 19290,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 19329,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 19393,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 19440,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "id": 19528,
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 19604,
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "id": 19641,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "longitude": 0.0
  },
  {
      "deviceid": 177657227,
      "devicetime": "2024-09-05T14:14:34.180Z",
      "protocol": "mqtt",
      "latitude": 0.0,
      "devicename": "Multitag_V2_03_EXP_2C:A7:74:A1:34:3A",
      "id": 19749,
      "longitude": 0.0
  }
]