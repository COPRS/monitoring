# Procedure to upgrade Performance Indicator #

This procedure will update Reference System Postgres database and add trigger requests to compute last version (30/07/2023) of Sentinel-1, Sentinel-2 and Sentinel-3 Performance Indicators.


## STEP 1 : upgrade database definition ##

Connect to RS Monitoring Postgresql and execute these SQL commands.
```
ALTER TABLE product
  ADD COLUMN IF NOT EXISTS ads_pi_type varchar(32),
  ADD COLUMN IF NOT EXISTS ads_late boolean DEFAULT false,
  ADD COLUMN IF NOT EXISTS ads_late1d boolean DEFAULT false,
  ADD COLUMN IF NOT EXISTS ads_late2d boolean DEFAULT false,
  ADD COLUMN IF NOT EXISTS ads_late3d boolean DEFAULT false,
  ADD COLUMN IF NOT EXISTS ads_late7d boolean DEFAULT false,
  ADD COLUMN IF NOT EXISTS ads_pi_timeliness_value_seconds integer DEFAULT 0;

ALTER TABLE missing_products  ADD COLUMN IF NOT EXISTS ads_pi_type varchar(32);

CREATE INDEX IF NOT EXISTS idx_ads_pi_type_product ON product(ads_pi_type);
CREATE INDEX IF NOT EXISTS idx_rs_chain_name_product ON processing(rs_chain_name);
CREATE INDEX IF NOT EXISTS idx_ads_pi_type_missing_products ON missing_products(ads_pi_type);

```



## STEP 2 : add procedure to fill new created fields ## 
Connect to RS Monitoring Postgresql and execute these SQL commands.

This triggering function aims to compute ***ads_pi_type*** for table  **missing_products**.
```                            
CREATE OR REPLACE FUNCTION insert_pi_type_into_missing_product() RETURNS TRIGGER  AS
$insert_pi_type_into_missing_product$
BEGIN
	IF (NEW.ads_pi_type IS NULL) THEN

/*-------------------------------------------*/
WITH subquery AS
(
SELECT
 CASE
/**************************************************/
/*                       S1                       */
/**************************************************/
/* AIO */
         WHEN  
               missing_products.product_metadata_custom->> 'platform_short_name_string'='SENTINEL-1' AND
               p.rs_chain_name = 'S1-L0AIOP' AND
              (missing_products.product_metadata_custom->> 'product_type_string' = 'GP_RAW__0_' OR
               missing_products.product_metadata_custom->> 'product_type_string' = 'HK_RAW__0_') 
              THEN 'S1-L0-HKGP'

          /* OTHER AIOP : Do not invert lines */
         WHEN  
               missing_products.product_metadata_custom->> 'platform_short_name_string'='SENTINEL-1' AND
               p.rs_chain_name = 'S1-L0AIOP' 
              THEN 'N/A:S1-L0-SEG'

/* L0ASP */
         /* detect WAVE L0ASP */
         WHEN missing_products.product_metadata_custom->> 'platform_short_name_string'='SENTINEL-1'  AND 
              p.rs_chain_name = 'S1-L0ASP' AND
              missing_products.product_metadata_custom->>'product_type_string'  IN ('WV_SLC__0S', 'WV_SLC__0A', 'WV_SLC__0C', 'WV_SLC__0N') 
              THEN 'S1-L0-WV'

         /* detect NRT L0ASP, without WAVE of course */
         WHEN missing_products.product_metadata_custom->> 'platform_short_name_string'='SENTINEL-1'  AND 
              p.rs_chain_name = 'S1-L0ASP' AND
               (
                    SELECT 
                         product.timeliness_name
                    FROM  
                         input_list_internal, product, processing
                    WHERE
                         input_list_internal.processing_id = processing.id AND
                         product.id = input_list_internal.product_id AND
                         processing.mission = 'S1'  AND
                         processing.id = p.id
                     LIMIT 1
               ) != 'S1_FAST24'
              THEN 'S1-L0-NRT'


          /* OTHER L0ASP : Do not invert lines */
         WHEN missing_products.product_metadata_custom->> 'platform_short_name_string'='SENTINEL-1'  AND 
              p.rs_chain_name = 'S1-L0ASP' 
              THEN 'N/A:S1-L0-F24'


/* L1 and L2 NRT */
         /* detect WAVE L1 or L2*/
         WHEN missing_products.product_metadata_custom->> 'platform_short_name_string'='SENTINEL-1'  AND 
              (p.rs_chain_name = 'S1-L1' OR p.rs_chain_name = 'S1-L2' ) AND
              missing_products.product_metadata_custom->>'product_type_string' IN ('WV_SLC__1S', 'WV_SLC__1A', 'WV_SLC__1C', 'WV_SLC__1N') 
              THEN 'S1-L12-WAVE'
          
         /* Detect L1 L2 NRT without WAVE of course */
         WHEN missing_products.product_metadata_custom->> 'platform_short_name_string'='SENTINEL-1'  AND 
              (p.rs_chain_name = 'S1-L1' OR p.rs_chain_name = 'S1-L2' ) AND
               (
                    SELECT 
                         product.timeliness_name
                    FROM  
                         input_list_internal, product, processing
                    WHERE
                         input_list_internal.processing_id = processing.id AND
                         product.id = input_list_internal.product_id AND
                         processing.mission = 'S1'  AND
                         processing.id = p.id
                     LIMIT 1
               ) != 'S1_FAST24' 
              THEN 'S1-L12-NRT'
              
         /* OTHER L1 L2 : Do not invert lines */
         WHEN missing_products.product_metadata_custom->> 'platform_short_name_string'='SENTINEL-1'  AND 
              (p.rs_chain_name = 'S1-L1' OR p.rs_chain_name = 'S1-L2' ) 
              THEN 'S1-L12-NTC'





/**************************************************/
/*                       S2                       */
/**************************************************/

/* L0u */
         WHEN missing_products.product_metadata_custom->> 'platform_short_name_string'='SENTINEL-2'  AND 
              p.rs_chain_name = 's2-l0u' AND
               (missing_products.product_metadata_custom->> 'product_type_string') IN ('AUX_SADATA', 'PRD_HKTM')
              THEN 'S2-L0-HKAN'


/* L0c */
         WHEN missing_products.product_metadata_custom->> 'platform_short_name_string'='SENTINEL-2'  AND 
              p.rs_chain_name = 's2-l0c'  
              THEN 'N/A:S2-L0'              

/* L1 */
         WHEN missing_products.product_metadata_custom->> 'platform_short_name_string'='SENTINEL-2'  AND 
              cast(missing_products.product_metadata_custom->> 'processing_level_integer' as integer) = '1' AND
              (missing_products.product_metadata_custom->> 'product_type_string') IN ('MSI_L1C_DS', 'MSI_L1C_TL', 'MSI_L1C_TC')
              THEN 'S2-L1C'              

/* L2 */
         WHEN missing_products.product_metadata_custom->> 'platform_short_name_string'='SENTINEL-2'  AND 
              cast(missing_products.product_metadata_custom->> 'processing_level_integer' as integer) = '2' AND
              (missing_products.product_metadata_custom->> 'product_type_string') IN ('MSI_L2A_DS', 'MSI_L2A_TL', 'MSI_L2A_TC')
              THEN 'S2-L2A'              

              
/**************************************************/
/*                       S3                       */
/**************************************************/

/* S3-ACQ  */
         WHEN missing_products.product_metadata_custom->> 'platform_short_name_string'='SENTINEL-3'  AND 
              p.rs_chain_name = 'S3-ACQ' 
              THEN 'N/A:S3-ISIP'


/*  L0P */
         WHEN missing_products.product_metadata_custom->> 'platform_short_name_string'='SENTINEL-3'  AND 
              p.rs_chain_name = 'S3-L0P' AND
              missing_products.product_metadata_custom->> 'instrument_short_name_string'='TM' 
              THEN 'S3-HKAN'    

/*  L0P with NRT products */
         WHEN missing_products.product_metadata_custom->> 'platform_short_name_string'='SENTINEL-3'  AND 
              p.rs_chain_name = 'S3-L0P' AND
              missing_products.product_metadata_custom->> 'instrument_short_name_string' <> 'TM' 
              THEN 'S3-NRT'                  

/* NRT  */
        WHEN missing_products.product_metadata_custom->> 'platform_short_name_string'='SENTINEL-3' AND
               p.rs_chain_name like '%-NRT'
             THEN 'S3-NRT'
             
        WHEN  p.rs_chain_name = 'S3-OL1-RAC-NRT'
             THEN 'S3-NRT'


/* STC */
        WHEN missing_products.product_metadata_custom->> 'platform_short_name_string'='SENTINEL-3'  AND
             p.rs_chain_name = 'S3-SY2-STC' 
             THEN 'S3-STC1'

        /* OTHER PUG-STC : DO NOT INVERT LINES */  
        WHEN missing_products.product_metadata_custom->> 'platform_short_name_string'='SENTINEL-3'  AND
             p.rs_chain_name <> 'S3-SY2-STC' AND
             p.rs_chain_name like '%-STC'
             THEN 'S3-STC2'

/* NTC */
        WHEN missing_products.product_metadata_custom->> 'platform_short_name_string'='SENTINEL-3'  AND
             p.rs_chain_name <> 'S3-SR1-NTC' AND
             p.rs_chain_name <> 'S3-MW1-NTC' AND
             p.rs_chain_name like '%-NTC'
             
             THEN 'S3-NTC1'

        /* OTHER PUG-NTC : DO NOT INVERT LINES */  
        WHEN missing_products.product_metadata_custom->> 'platform_short_name_string'='SENTINEL-3'  AND
             (p.rs_chain_name = 'S3-SR1-NTC' OR
             p.rs_chain_name = 'S3-MW1-NTC')
             THEN 'S3-NTC2'

         ELSE 'UNDEFINED'

  END AS pi_type
  
FROM missing_products, processing p
where 
    p.id =  missing_products.processing_failed_id AND
    missing_products.id = NEW.id

) /* end of subquery */
/*-------------------------------------------*/


UPDATE 
       missing_products
SET 
       ads_pi_type = subquery.pi_type
FROM  
       subquery
WHERE 
        missing_products.id = NEW.id;

END IF;

	RETURN NEW;
END;
 $insert_pi_type_into_missing_product$  LANGUAGE plpgsql;

```

This triggering function aims to compute ***ads_pi_type*** for table **product**.

```                            
CREATE OR REPLACE FUNCTION insert_pi_type_into_product() RETURNS TRIGGER  AS
$insert_pi_type_into_product$
BEGIN
	IF ( 
          (NEW.ads_pi_type IS NULL) or 
          (NEW.ads_pi_type='bad') or 
          (NEW.ads_pi_type='bad2') 
       ) 
     THEN

/*-------------------------------------------*/
WITH subquery AS
(
SELECT

 CASE
/**************************************************/
/*                       S1                       */
/**************************************************/
/* AIO */
         WHEN  
               product.custom->> 'platform_short_name_string'='SENTINEL-1' AND
               processing.rs_chain_name = 'S1-L0AIOP' AND
              (product.custom->> 'product_type_string' = 'GP_RAW__0_' OR
               product.custom->> 'product_type_string' = 'HK_RAW__0_') 
              THEN 'S1-L0-HKGP'

         WHEN  
               product.custom->> 'platform_short_name_string'='SENTINEL-1' AND
               processing.rs_chain_name = 'S1-L0AIOP' AND
              NOT (product.custom->> 'product_type_string' = 'GP_RAW__0_' OR
                   product.custom->> 'product_type_string' = 'HK_RAW__0_') 
              THEN 'N/A:S1-L0-SEG'

/* L0ASP */
         WHEN product.custom->> 'platform_short_name_string'='SENTINEL-1'  AND 
              processing.rs_chain_name = 'S1-L0ASP' AND
              (product.custom->> 'operational_mode_string') != 'WV' AND
              product.timeliness_name != 'S1_FAST24' 
              THEN 'S1-L0-NRT'

         WHEN product.custom->> 'platform_short_name_string'='SENTINEL-1'  AND 
              processing.rs_chain_name = 'S1-L0ASP' AND
              (product.custom->> 'operational_mode_string') = 'WV'
              THEN 'S1-L0-WV'

         WHEN product.custom->> 'platform_short_name_string'='SENTINEL-1'  AND 
              processing.rs_chain_name = 'S1-L0ASP' AND
              (product.custom->> 'operational_mode_string') != 'WV' AND              
              product.timeliness_name ='S1_FAST24' 
              THEN 'N/A:S1-L0-F24'


/* L1 and L2 NRT */
         WHEN product.custom->> 'platform_short_name_string'='SENTINEL-1'  AND 
              (processing.rs_chain_name = 'S1-L1' OR processing.rs_chain_name = 'S1-L2' ) AND             
              (product.custom->> 'product_type_string' not like '%WV%') AND
              product.timeliness_name != 'S1_FAST24' 
              THEN 'S1-L12-NRT'

         WHEN product.custom->> 'platform_short_name_string'='SENTINEL-1'  AND 
              (processing.rs_chain_name = 'S1-L1' OR processing.rs_chain_name = 'S1-L2' ) AND
              (product.custom->> 'product_type_string' not like '%WV%') AND
              product.timeliness_name = 'S1_FAST24' 
              THEN 'S1-L12-NTC'


         WHEN product.custom->> 'platform_short_name_string'='SENTINEL-1'  AND 
              (processing.rs_chain_name = 'S1-L1' OR processing.rs_chain_name = 'S1-L2' ) AND
              (product.custom->> 'product_type_string' like '%WV%') 
              THEN 'S1-L12-WV'


/**************************************************/
/*                       S2                       */
/**************************************************/

/* L0u */
         WHEN processing.rs_chain_name = 's2-l0u' AND 
              product.filename like '%SADATA%'
              THEN 'S2-L0-HKAN'

         WHEN processing.rs_chain_name = 's2-l0u' AND
              product.filename like '%HKTM%'
              THEN 'S2-L0-HKAN'

/* L0c */
         WHEN processing.rs_chain_name = 's2-l0c'  
              THEN 'N/A:S2-L0'              

/* L1 */
         WHEN product.custom->> 'platform_short_name_string'='SENTINEL-2'  AND 
              cast(product.custom->> 'processing_level_integer' as integer) = '1' AND
              (product.custom->> 'product_type_string') IN ('MSI_L1C_DS', 'MSI_L1C_TL', 'MSI_L1C_TC')
              THEN 'S2-L1C'              

/* L2 */
         WHEN product.custom->> 'platform_short_name_string'='SENTINEL-2'  AND 
              cast(product.custom->> 'processing_level_integer' as integer) = '2' AND
              (product.custom->> 'product_type_string') IN ('MSI_L2A_DS', 'MSI_L2A_TL', 'MSI_L2A_TC')
              THEN 'S2-L2A'              

              
/**************************************************/
/*                       S3                       */
/**************************************************/
        WHEN product.custom->> 'platform_short_name_string'='SENTINEL-3' AND
             processing.rs_chain_name IN ('S3-ACQ')      
             THEN 'N/A:S3-ISIP'

         WHEN  product.custom->> 'platform_short_name_string'='SENTINEL-3'  AND 
              processing.rs_chain_name IN ('S3-L0P') AND
              (product.custom->> 'product_type_string') IN ('TM_0_HKM2__', 'TM_0_NAT___', 'TM_0_HKM___') 
              THEN 'S3-HKAN'
 

        WHEN product.custom->> 'platform_short_name_string'='SENTINEL-3' AND
             product.filename like '%_NR_%' AND
             processing.rs_chain_name <> 'S3-ACQ' AND
             (product.custom->> 'product_type_string') NOT IN ('TM_0_HKM2__', 'TM_0_NAT___', 'TM_0_HKM___') 
             THEN 'S3-NRT'


        WHEN product.custom->> 'platform_short_name_string'='SENTINEL-3' AND
             product.filename like '%_ST_%' AND 
             processing.rs_chain_name = 'S3-SY2-STC' 
             THEN 'S3-STC1'

        WHEN product.custom->> 'platform_short_name_string'='SENTINEL-3' AND
             product.filename like '%_ST_%' AND 
             NOT (processing.rs_chain_name = 'S3-SY2-STC') 
             THEN 'S3-STC2'


        WHEN product.custom->> 'platform_short_name_string'='SENTINEL-3' AND
             product.filename like '%_NT_%' AND 
             NOT (processing.rs_chain_name = 'S3-MW1-NTC'  OR processing.rs_chain_name = 'S3-SR1-NTC')
             THEN 'S3-NTC1'

        WHEN product.custom->> 'platform_short_name_string'='SENTINEL-3' AND
             product.filename like '%_NT_%' AND 
             ( processing.rs_chain_name = 'S3-MW1-NTC'  OR processing.rs_chain_name = 'S3-SR1-NTC') 
             THEN 'S3-NTC2'
         ELSE 'UNDEFINED'

  END AS pi_type 

FROM product, processing, output_list
where 
    processing.id = output_list.processing_id AND
    output_list.product_id = NEW.id AND
    product.id = NEW.id
) /* end of subquery */


UPDATE 
       product  
SET 
       ads_pi_type = subquery.pi_type
FROM  
       subquery
WHERE 
       product.id = NEW.id;
	END IF;
	RETURN NEW;
END;
 $insert_pi_type_into_product$  LANGUAGE plpgsql;

```

This triggering function aims to compute timeliness fields for table **product**.

```
CREATE OR REPLACE FUNCTION insert_timeliness_into_product() RETURNS TRIGGER  AS
$insert_timeliness_into_product$
BEGIN
	IF (NEW.ads_pi_type IS NOT NULL) THEN

/*-------------------------------------------*/
WITH subquery AS
(
SELECT CASE
          WHEN  ads_pi_type = 'S1-L0-HKGP'  THEN 1800
          WHEN  ads_pi_type = 'S1-L0-NRT'   THEN 5400
          WHEN  ads_pi_type = 'S1-L0-WV'    THEN 3600
          WHEN  ads_pi_type = 'S1-L12-WV'   THEN 10800 
          WHEN  ads_pi_type = 'S1-L12-NRT'  THEN 10800
          WHEN  ads_pi_type = 'S1-L12-NTC'  THEN 21600
          WHEN  ads_pi_type = 'S1-L2-WV'    THEN 10800
          WHEN  ads_pi_type = 'S2-L0-HKAN'  THEN 1800
          WHEN  ads_pi_type = 'S2-L1C'      THEN 4500
          WHEN  ads_pi_type = 'S2-L2A'      THEN 7200
          WHEN  ads_pi_type = 'S3-HKAN'     THEN 1800
          WHEN  ads_pi_type = 'S3-NRT'      THEN 3600
          WHEN  ads_pi_type = 'S3-STC1'     THEN 72000
          WHEN  ads_pi_type = 'S3-STC2'     THEN 158400
          WHEN  ads_pi_type = 'S3-NTC1'     THEN 252000
          WHEN  ads_pi_type = 'S3-NTC2'     THEN 2505600
          ELSE -1
END AS pi_timeliness_value_corrected_seconds

FROM product
where 
    product.id = NEW.id

) /* end of subquery */


UPDATE 
       product  
SET 
       ads_pi_timeliness_value_seconds = subquery.pi_timeliness_value_corrected_seconds
FROM  
       subquery
WHERE 
       product.id = NEW.id;

	END IF;

RETURN NEW;

END;
 $insert_timeliness_into_product$  LANGUAGE plpgsql;
```

This triggering function aims to compute timeliness fields for table **product**.

```
CREATE OR REPLACE FUNCTION insert_timeliness_into_product() RETURNS TRIGGER  AS
$insert_timeliness_into_product$
BEGIN
	IF (NEW.ads_pi_type IS NOT NULL) THEN

/*-------------------------------------------*/
WITH subquery AS
(
SELECT CASE
          WHEN  ads_pi_type = 'S1-L0-HKGP'  THEN 1800
          WHEN  ads_pi_type = 'S1-L0-NRT'   THEN 5400
          WHEN  ads_pi_type = 'S1-L0-WV'    THEN 3600
          WHEN  ads_pi_type = 'S1-L12-WV'   THEN 10800 
          WHEN  ads_pi_type = 'S1-L12-NRT'  THEN 10800
          WHEN  ads_pi_type = 'S1-L12-NTC'  THEN 21600
          WHEN  ads_pi_type = 'S1-L2-WV'    THEN 10800
          WHEN  ads_pi_type = 'S2-L0-HKAN'  THEN 1800
          WHEN  ads_pi_type = 'S2-L1C'      THEN 4500
          WHEN  ads_pi_type = 'S2-L2A'      THEN 7200
          WHEN  ads_pi_type = 'S3-HKAN'     THEN 1800
          WHEN  ads_pi_type = 'S3-NRT'      THEN 3600
          WHEN  ads_pi_type = 'S3-STC1'     THEN 72000
          WHEN  ads_pi_type = 'S3-STC2'     THEN 158400
          WHEN  ads_pi_type = 'S3-NTC1'     THEN 252000
          WHEN  ads_pi_type = 'S3-NTC2'     THEN 2505600
          ELSE -1
END AS pi_timeliness_value_corrected_seconds

FROM product
where 
    product.id = NEW.id

) /* end of subquery */


UPDATE 
       product  
SET 
       ads_pi_timeliness_value_seconds = subquery.pi_timeliness_value_corrected_seconds
FROM  
       subquery
WHERE 
       product.id = NEW.id;

	END IF;
	
RETURN NEW;

END;
 $insert_timeliness_into_product$  LANGUAGE plpgsql;
```


This triggering function aims to compute late fields for table **product**.

```
CREATE OR REPLACE FUNCTION insert_late_into_product() RETURNS TRIGGER  AS
$insert_late_into_product$

BEGIN
	IF (NEW.ads_pi_timeliness_value_seconds > 0) THEN
		 

/*-------------------------------------------*/
WITH subquery AS
(
SELECT     
CASE WHEN ((EXTRACT(EPOCH FROM (product.prip_storage_end_date - product.t0_pdgs_date)) > ads_pi_timeliness_value_seconds) 
                                or (product.prip_storage_end_date is null)) THEN TRUE ELSE FALSE END AS late,
CASE WHEN ((EXTRACT(EPOCH FROM (product.prip_storage_end_date - product.t0_pdgs_date)) > ads_pi_timeliness_value_seconds + 86400) 
                                or (product.prip_storage_end_date is null)) THEN TRUE ELSE FALSE END AS late1d,
CASE WHEN ((EXTRACT(EPOCH FROM (product.prip_storage_end_date - product.t0_pdgs_date)) > ads_pi_timeliness_value_seconds + 172800) 
                                or (product.prip_storage_end_date is  null)) THEN TRUE ELSE FALSE END AS late2d,
CASE WHEN ((EXTRACT(EPOCH FROM (product.prip_storage_end_date - product.t0_pdgs_date)) > ads_pi_timeliness_value_seconds + 259200) 
                                or (product.prip_storage_end_date is  null)) THEN TRUE ELSE FALSE END AS late3d,
CASE WHEN ((EXTRACT(EPOCH FROM (product.prip_storage_end_date - product.t0_pdgs_date)) > ads_pi_timeliness_value_seconds + 604800) 
                                or (product.prip_storage_end_date is  null)) THEN TRUE ELSE FALSE END AS late7d

FROM product
where 
    product.id = NEW.id

) /* end of subquery */


UPDATE 
       product  
SET 
       ads_late = subquery.late,
       ads_late1d = subquery.late1d,
       ads_late2d = subquery.late2d,
       ads_late3d = subquery.late3d,
       ads_late7d = subquery.late7d
FROM  
       subquery
WHERE 
       product.id = NEW.id;


	END IF;

	RETURN NEW;

END;
 $insert_late_into_product$  LANGUAGE plpgsql;
 ```


 ## STEP 3 : arm triggering functions ## 
 
Connect to RS Monitoring Postgresql and execute these SQL commands.
Finally these commands will  triggering function aims to compute late fields for table **product**.

```

CREATE OR REPLACE TRIGGER insert_pi_type_into_product
    AFTER UPDATE OF custom ON product
    FOR EACH ROW EXECUTE PROCEDURE insert_pi_type_into_product();

CREATE OR REPLACE TRIGGER insert_pi_type_into_product2
    AFTER INSERT ON product
    FOR EACH ROW EXECUTE PROCEDURE insert_pi_type_into_product();

CREATE OR REPLACE TRIGGER insert_timeliness_into_product
    AFTER UPDATE OF ads_pi_type ON product
    FOR EACH ROW EXECUTE PROCEDURE insert_timeliness_into_product(); 


CREATE OR REPLACE TRIGGER insert_late_into_product
   AFTER UPDATE OF prip_storage_end_date ON product
    FOR EACH ROW EXECUTE PROCEDURE insert_late_into_product();
    
CREATE OR REPLACE TRIGGER insert_pi_type_into_missing_product
    AFTER INSERT ON missing_products
    FOR EACH ROW EXECUTE PROCEDURE insert_pi_type_into_missing_product();
    

```


## PI computing requests ## 
 To retrieve PI values, you can use this requests.
 
 For completeness.
 ```
/* PRODUCTS on time without delay */
WITH production AS (
  SELECT 
    ads_pi_type,
    processing.mission,
    COUNT(CASE WHEN (ads_late=false and duplicate=false) THEN 1 ELSE NULL END) AS on_time_0d,
    COUNT(CASE WHEN (ads_late1d=false and duplicate=false) THEN 1 ELSE NULL END) AS on_time_1d,
    COUNT(CASE WHEN (ads_late2d=false and duplicate=false) THEN 1 ELSE NULL END) AS on_time_2d,
    COUNT(CASE WHEN (ads_late3d=false and duplicate=false) THEN 1 ELSE NULL END) AS on_time_3d,
    COUNT(CASE WHEN (ads_late7d=false and duplicate=false) THEN 1 ELSE NULL END) AS on_time_7d,
    COUNT(CASE WHEN (prip_storage_end_date is null  and duplicate=false) THEN 1 ELSE NULL END) AS missing_prip,
    COUNT (CASE WHEN (duplicate=false) THEN 1 ELSE NULL END) as  all_generated
  FROM 
    product, processing, output_list
  WHERE
    /* Only non duplicated processing output */
    processing.duplicate = false AND
    processing.id = output_list.processing_id AND
    output_list.product_id = product.id AND

    /* product get on the time frame */
    (processing.processing_date BETWEEN $__timeFrom() AND $__timeTo()) and

    /* remove unwanted PI type */
    ads_pi_type NOT in ('UNDEFINED','N/A:S3-ISIP','N/A:S2-L0','N/A:S1-L0-SEG','N/A:S1-L0-F24') 
  GROUP BY 
    ads_pi_type, mission
), 



/* PRODUCTS missing */
loss AS (
  SELECT 
    missing_products.ads_pi_type,
    processing.mission,
    SUM(estimated_count) AS missing_count
  FROM 
    missing_products, processing
  WHERE

    /* Only non duplicated processing output */
    processing.duplicate = false AND
    processing.id = missing_products.processing_failed_id AND


    /* product get on the time frame */
    (processing.processing_date BETWEEN $__timeFrom() AND $__timeTo()) and

    /* remove unwanted PI type */
    missing_products.ads_pi_type NOT in ('UNDEFINED','N/A:S3-ISIP','N/A:S2-L0','N/A:S1-L0-SEG','N/A:S1-L0-F24') 


  GROUP BY 
    ads_pi_type, mission
)

  SELECT 
    CONCAT('PI-COMPLETENESS-', COALESCE (p.ads_pi_type, l.ads_pi_type)) as "PI", 
    COALESCE (p.mission, l.mission) as "mission",
    p.on_time_0d as t0d,
    p.on_time_1d as t1d,
    p.on_time_2d as t2s,
    p.on_time_3d as t3d,
    p.on_time_7d as t7d,
    p.all_generated as "generated",
    l.missing_count as "not generated",
    p.missing_prip as "not on prip",
    (p.all_generated +   COALESCE(l.missing_count,0)) as expected,

    CAST (COALESCE(on_time_0d,0) as DECIMAL) / NULLIF(( COALESCE(all_generated,0) + COALESCE(l.missing_count,0)),0) as "Completeness 0d",
    CAST (COALESCE(on_time_1d,0) as DECIMAL) / NULLIF(( COALESCE(all_generated,0) + COALESCE(l.missing_count,0)),0) as "Completeness 1d",
    CAST (COALESCE(on_time_2d,0) as DECIMAL) / NULLIF(( COALESCE(all_generated,0) + COALESCE(l.missing_count,0)),0) as "Completeness 2d",
    CAST (COALESCE(on_time_3d,0) as DECIMAL) / NULLIF(( COALESCE(all_generated,0) + COALESCE(l.missing_count,0)),0) as "Completeness 3d",
    CAST (COALESCE(on_time_7d,0) as DECIMAL) / NULLIF(( COALESCE(all_generated,0) + COALESCE(l.missing_count,0)),0) as "Completeness 7d"
  FROM 
    production p FULL JOIN  loss l ON p.ads_pi_type = l.ads_pi_type

 ```

For timeliness.
 ```
  
/* extract on_time_0d and all_generated */
WITH production AS (
  SELECT 
    ads_pi_type,
    mission,
    COUNT(CASE WHEN (ads_late=false and duplicate=false) THEN 1 ELSE NULL END) AS on_time_0d,
    COUNT (CASE WHEN (duplicate=false) THEN 1 ELSE NULL END) as  all_generated
  FROM 
    product, processing, output_list
  WHERE
    /* Only non duplicated processing output */
    processing.duplicate = false AND
    processing.id = output_list.processing_id AND
    output_list.product_id = product.id AND
    (processing.processing_date BETWEEN $__timeFrom() AND $__timeTo()) AND 
     ads_pi_type NOT in ('UNDEFINED','N/A:S3-ISIP','N/A:S2-L0','N/A:S1-L0-SEG','N/A:S1-L0-F24')
    AND NOT (product.prip_storage_end_date is NULL)
  GROUP BY 
    ads_pi_type, mission
)


SELECT 
   CONCAT('PI-TIMELINESS-', ads_pi_type) as "PI", 
   mission,
   on_time_0d,
   all_generated,
   CAST (COALESCE(on_time_0d,0) as DECIMAL) / NULLIF(( COALESCE(all_generated,0) ),0) as "Timeliness"
FROM 
  production

 ```
