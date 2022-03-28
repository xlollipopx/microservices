import axios from "axios";
import * as AxiosLogger from "axios-logger"
import jsCookie from "js-cookie";

const instance = axios.create({
    withCredentials: true,
    headers: {
        "Content-Type": "application/json",
        "Authorization": jsCookie.get('token'),
        "Access-Control-Allow-Origin": "*",
        "Access-Control-Expose-Headers": "*"
    }
});

instance.interceptors.request.use(AxiosLogger.requestLogger);
instance.interceptors.response.use(AxiosLogger.responseLogger);

export default instance