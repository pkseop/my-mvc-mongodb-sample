package my.spring.sample.mvc.component;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.route53.Route53Client;
import software.amazon.awssdk.services.route53.model.*;

import java.util.List;

@Slf4j
@Component
public class AwsRoute53Handler {

    @Value("${aws.route53.hosted-zone-id}")
    private String hostedZoneId;

    @Value("${aws.route53.alias-target.hosted-zone-id}")
    private String aliasTargetHostedZoneId;

    @Value("${aws.route53.alias-target.dns-name}")
    private String aliasTargetDnsName;

    @Autowired
    private Route53Client route53Client;

    private void changeResourceRecordeSet(String subdomain, ChangeAction changeAction) {
        String domainName = subdomain + ".archisketch.com";

        AliasTarget aliasTarget = AliasTarget.builder()
                .hostedZoneId(aliasTargetHostedZoneId)
                .dnsName(aliasTargetDnsName)
                .evaluateTargetHealth(true)
                .build();

        ResourceRecordSet rrs = ResourceRecordSet.builder()
                .name(domainName)
                .type(RRType.A)
                .aliasTarget(aliasTarget)
                .build();

        Change change = Change.builder()
                .action(changeAction)
                .resourceRecordSet(rrs)
                .build();

        ChangeBatch changeBatch = ChangeBatch.builder()
                .changes(change)
                .build();

        ChangeResourceRecordSetsRequest crrsr = ChangeResourceRecordSetsRequest.builder()
                .hostedZoneId(hostedZoneId)
                .changeBatch(changeBatch)
                .build();

        route53Client.changeResourceRecordSets(crrsr);
    }

    public void createRecord(String subdomain) {
        this.changeResourceRecordeSet(subdomain, ChangeAction.CREATE);
    }

    public void deleteRecord(String subdomain) {
        this.changeResourceRecordeSet(subdomain, ChangeAction.DELETE);
    }

    public List<String> recordNameList() {
        ListResourceRecordSetsRequest request = ListResourceRecordSetsRequest.builder()
                .hostedZoneId(hostedZoneId)
                .build();

        ListResourceRecordSetsResponse listResourceRecordSets = route53Client.listResourceRecordSets(request);
        List<ResourceRecordSet> records = listResourceRecordSets.resourceRecordSets();

        List<String> domainList = Lists.newArrayList();
        for (ResourceRecordSet record : records) {
            if(record.name().equals("archisketch.com."))
                continue;

            domainList.add(record.name());
        }
        return domainList;
    }
}
