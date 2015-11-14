package com.aexp.jmai.qlexam.service;

import com.aexp.jmai.qlexam.domain.GiantBomb;
import retrofit.http.GET;
import retrofit.http.Query;

public interface QueryClient {
    @GET("/search/") GiantBomb getGames(@Query("api_key") String key,
                                               @Query("query") String query,
                                               @Query("format") String format,
                                               @Query("resources") String resources);
}
