import axios from "axios"

var defaultApi = "http://localhost:8080/api";


export const fetchPositions = () => {
  return axios.get(defaultApi +'/positions')
}


