package us.pojo.silentauction.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import us.pojo.silentauction.repository.ItemRepository;

@Controller
public class AuctionController {

    @Autowired
    private ItemRepository items;
    
//    @RequestMapping(value="/items.html", method={RequestMethod.GET})
//    public ModelAndView getItems(@RequestParam(name="filter", required=false) String filter) {
//        ModelAndView model = new ModelAndView("items");
//        model.addObject("items", items.findAll());
//        return model;
//    }
}
