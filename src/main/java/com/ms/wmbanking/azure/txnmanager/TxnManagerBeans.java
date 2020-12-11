package com.ms.wmbanking.azure.txnmanager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Slf4j
@Import(TxnmanagerUpdate.class)
public class TxnManagerBeans {

}
