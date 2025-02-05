package com.cryptomanager.Controller;

import com.cryptomanager.Model.Crypto;
import com.cryptomanager.Service.CryptoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;


@RestController
public class CryptoController {
    private final CryptoService cryptoService;

    public CryptoController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @GetMapping("/cryptos/{id}")
    public ResponseEntity<?> getCryptoById (@PathVariable int id){
        Crypto crypto = cryptoService.getCryptoById(id);

        if(crypto == null){
            return new ResponseEntity<>("Crypto is not found under id: " + id, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(crypto, HttpStatus.OK);
    }

    @PostMapping("/cryptos")
    public ResponseEntity<?> addCrypto(@RequestBody Crypto crypto){
        cryptoService.addCrypto(crypto);
        return new ResponseEntity<>("Crypto has been added",HttpStatus.CREATED);
    }

    @GetMapping("/cryptos")
    public ResponseEntity<?> getCryptoPortfolio(@RequestParam(required = false) String sort){
        ArrayList<Crypto> listCryptoPortfolio = cryptoService.getListOfCryptoPortfolio();

        if(listCryptoPortfolio == null || listCryptoPortfolio.isEmpty()){
            if (sort != null && !(sort.equals("name") || sort.equals("price") || sort.equals("quantity"))){
                return new ResponseEntity<>("Wrong parameter entered, expected parameters: 'name', 'price' or 'quantity'", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        if(sort == null){
            return new ResponseEntity<>(listCryptoPortfolio, HttpStatus.OK);
        }

        switch (sort) {
            case "name":
                return new ResponseEntity<>(cryptoService.getSortedByNameListOfCryptoPortfolio(), HttpStatus.OK);

            case "price":
                return new ResponseEntity<>(cryptoService.getSortedByPriceListOfCryptoPortfolio(), HttpStatus.OK);

            case "quantity":
                return new ResponseEntity<>(cryptoService.getSortedByQuantityListOfCryptoPortfolio(), HttpStatus.OK);

            default:
                return new ResponseEntity<>("Wrong parameter entered, expected parameters: 'name', 'price' or 'quantity'", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/cryptos/{id}")
    public ResponseEntity<?> updateCryptoById(@PathVariable int id, @RequestBody Crypto cryptoToUpdate){
        Crypto modifiedCrypto = cryptoService.updateCryptoById(id, cryptoToUpdate);

        if(modifiedCrypto == null){
            return new ResponseEntity<>("Crypto is not found under id: " + id, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Crypto has been updated", HttpStatus.OK);

    }
    // endpoint bez /cryptos podle zadani:
    // Přidejte endpoint pro výpočet celkové hodnoty portfolia (GET /portfolio-value)
    @GetMapping("/portfolio-value")
    public ResponseEntity<?> getPortfolioTotal() {
        BigDecimal totalValue= cryptoService.calculatePortfolioTotal();

        if(totalValue == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        if(totalValue.compareTo(BigDecimal.ZERO) == 0){
            return new ResponseEntity<>("Portfolio value is 0 because all cryptos have quantity 0",HttpStatus.OK);
        }
        return new ResponseEntity<>("Total value is: " + totalValue, HttpStatus.OK);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
       return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
