package com.example.sramdisplay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@ResponseBody
public class DisplayController {
    @Autowired
    private CassandraService cassandraService;
    @RequestMapping("/getDie")
    public List<DieDisplayData> getDie() {
        return cassandraService.getDieDisplayData(1, 1, 1, 32);
    }
}
