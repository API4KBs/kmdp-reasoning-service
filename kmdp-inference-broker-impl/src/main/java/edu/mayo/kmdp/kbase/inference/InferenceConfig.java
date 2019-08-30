package edu.mayo.kmdp.kbase.inference;

import edu.mayo.kmdp.util.ws.JsonRestWSUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
@ComponentScan
@Profile({"default", "inmemory"})
public class InferenceConfig {


    @Bean
    public MappingJackson2HttpMessageConverter customJson() {
        return JsonRestWSUtils.jacksonFHIRAdapter(JsonRestWSUtils.WithFHIR.STU3);
    }

}
