import axios from "axios"

var defaultApi = "http://backend:8080/api";


export const fetchPositions = () => {
  return axios.get(defaultApi +'/positions')
}


