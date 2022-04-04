/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package go.pajak.pbb.app.registrasi.dtomodel;

import lombok.*;

import java.io.Serializable;

/**
 *
 * @author Azinar
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseModel implements Serializable {
    
     Integer kodeResponse;
     String message;
     Object objResponse;

}
