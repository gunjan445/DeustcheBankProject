package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class Fund {

  @NotNull
  @NotEmpty
  @ApiModelProperty(notes = " Account which will be deducted")
  private final String fromAccountId;
  
  @NotNull
  @NotEmpty
  @ApiModelProperty(notes = " Account which will be credited")
  private final String toAccountId;
  
  @NotNull
  @Min(value = 0, message = "Balance must be positive.")
  @ApiModelProperty(notes = " Account Balance")
  private BigDecimal balance;

  public Fund(String fromAccountId,String toAccountId) {
    this.fromAccountId = fromAccountId;
    this.toAccountId=toAccountId;
    this.balance = BigDecimal.ZERO;
  }

  @JsonCreator
  public Fund(@JsonProperty("fromAccountId") String fromAccountId,@JsonProperty("toAccountId") String toAccountId,
    @JsonProperty("balance") BigDecimal balance) {
	  this.fromAccountId = fromAccountId;
	  this.toAccountId=toAccountId;
	  this.balance = balance;
  }
}
