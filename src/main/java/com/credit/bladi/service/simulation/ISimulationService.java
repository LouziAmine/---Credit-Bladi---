package com.credit.bladi.service.simulation;

import com.credit.bladi.model.simulation.request.SimulationRequest;
import com.credit.bladi.model.simulation.response.SimulationResponse;

public interface ISimulationService {

    SimulationResponse simulate(SimulationRequest request);
}
