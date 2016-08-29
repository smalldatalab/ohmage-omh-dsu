/*
Here is the format of the insert:

INSERT INTO oauth_client_details
            (client_id,
             resource_ids,
             client_secret,
             scope,
             authorized_grant_types,
             web_server_redirect_uri,
             authorities,
             access_token_validity,
             refresh_token_validity,
             additional_information,
             autoapprove)
VALUES      ('CLIENT-ID',
             'dataPoint',
             'CLIENT-SECRET',
             'read_data_points,write_data_points',
             'implicit',
             'http://localhost:3000/mobility_dashboard',
             'ROLE_CLIENT',
             NULL,
             -1,
             NULL,
             'read_data_points,write_data_points');
*/

INSERT INTO oauth_client_details
            (client_id,
             resource_ids,
             client_secret,
             scope,
             authorized_grant_types,
             web_server_redirect_uri,
             authorities,
             access_token_validity,
             refresh_token_validity,
             additional_information,
             autoapprove)
VALUES      ('io.smalldatalab.android.pam','dataPoint','fMNqTw27vwltUqHceFUgqLkHplxuv2vROBpuoy4GkoPVTuFCsV','write_data_points','authorization_code,refresh_token,password','oauth://callback','ROLE_CLIENT',null,-1,null,'write_data_points');

INSERT INTO oauth_client_details
            (client_id,
             resource_ids,
             client_secret,
             scope,
             authorized_grant_types,
             web_server_redirect_uri,
             authorities,
             access_token_validity,
             refresh_token_validity,
             additional_information,
             autoapprove)
VALUES      ('org.openmhealth.ios.pam','dataPoint','Rtg43jkLD7z76c','write_data_points','authorization_code,refresh_token,password','oauth://callback','ROLE_CLIENT',null,-1,null,'write_data_points');

INSERT INTO oauth_client_details
            (client_id,
             resource_ids,
             client_secret,
             scope,
             authorized_grant_types,
             web_server_redirect_uri,
             authorities,
             access_token_validity,
             refresh_token_validity,
             additional_information,
             autoapprove)
VALUES      ('io.smalldatalab.android.ohmage','dataPoint','xEUJgIdS2f12jmYomzEH','write_data_points','authorization_code,refresh_token,password','oauth://callback','ROLE_CLIENT',null,-1,null,'write_data_points');

INSERT INTO oauth_client_details
            (client_id,
             resource_ids,
             client_secret,
             scope,
             authorized_grant_types,
             web_server_redirect_uri,
             authorities,
             access_token_validity,
             refresh_token_validity,
             additional_information,
             autoapprove)
VALUES      ('io.smalldatalab.ios.ohmage','dataPoint','Rtg43jkLD7z76c','write_data_points','authorization_code,refresh_token,password','oauth://callback','ROLE_CLIENT',null,-1,null,'write_data_points');

INSERT INTO oauth_client_details
            (client_id,
             resource_ids,
             client_secret,
             scope,
             authorized_grant_types,
             web_server_redirect_uri,
             authorities,
             access_token_validity,
             refresh_token_validity,
             additional_information,
             autoapprove)
VALUES      ('io.smalldatalab.android.mobility','dataPoint','YLt2yYCtfxII164MWS1DsuGqkwnoXa9TpNUSTMhDXLLZy4VEWLf0PeULnGyrgv','write_data_points','authorization_code,refresh_token,password','oauth://callback','ROLE_CLIENT',null,-1,null,'write_data_points');

INSERT INTO oauth_client_details
            (client_id,
             resource_ids,
             client_secret,
             scope,
             authorized_grant_types,
             web_server_redirect_uri,
             authorities,
             access_token_validity,
             refresh_token_validity,
             additional_information,
             autoapprove)
VALUES      ('io.smalldatalab.ios.mobility','dataPoint','Rtg43jkLD7z76c','write_data_points','authorization_code,refresh_token,password','oauth://callback','ROLE_CLIENT',null,-1,null,'write_data_points');

INSERT INTO oauth_client_details
            (client_id,
             resource_ids,
             client_secret,
             scope,
             authorized_grant_types,
             web_server_redirect_uri,
             authorities,
             access_token_validity,
             refresh_token_validity,
             additional_information,
             autoapprove)
VALUES      ('mobility-visualization','dataPoint','xEUJgIdS2f12jmYomzEHIcpeG1Fbg2','read_data_points','implicit','oauth://callback','ROLE_CLIENT',null,-1,null,'read_data_points');
