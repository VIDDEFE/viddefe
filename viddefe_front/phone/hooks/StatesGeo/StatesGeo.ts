import api from "@/api";
import { getStatesGeo } from "@/api/StatesGeo";
import { ApiResponse } from "@/types/api";
import { Cities, States } from "@/types/StatesGeo";
import { useEffect, useState } from "react";

export default function useGeoData() {
  const [states, setStates] = useState<States[]>([]);
  const [cities, setCities] = useState<Cities[]>([]);
  const [selectedState, setSelectedState] = useState<States | null>(null);
  const [selectedCity, setSelectedCity] = useState<Cities | null>(null);
  const [loadingStates, setLoadingStates]  = useState<boolean>(false);

  useEffect(() => {
    const fetchStates = async () => {
        try{
            const response = await getStatesGeo()
            setLoadingStates(true);
            setStates(response? response : [])
            setLoadingStates(false)
        }catch(error){
            console.error("Error fetching states:", error);
        }
    }
    fetchStates();
}, []);

  useEffect(() => {
    if (!selectedState) {
      setCities([]);
      return;
    }
    const fetchCities = async () => {
      try {
        const response = await api.get<ApiResponse<Cities[]>>(`/states/${selectedState?.id}/cities`);
        setCities(response.data.data ? response.data.data : []);
      }catch(error){
        console.error("Error fetching cities:", error);
      }
    }
    fetchCities();
  }, [selectedState]);

  return {
    states,
    cities,
    selectedState,
    setSelectedState,
    selectedCity,
    setSelectedCity,
  };
}
