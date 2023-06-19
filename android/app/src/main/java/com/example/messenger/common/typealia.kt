package com.example.messenger.common

import com.example.messenger.data.network.adapter.NetworkResponse
import com.example.messenger.data.network.models.error.ApiErrorBody

typealias GenericResponse<S> = NetworkResponse<S, ApiErrorBody>