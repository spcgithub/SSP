Ext.define('Ssp.controller.admin.crg.AssociateChallengeReferralsAdminViewController', {
	extend: 'Ssp.controller.admin.AdminItemAssociationViewController',
    config: {
        associatedItemType: 'challengeReferral',
        parentItemType: 'challenge',
        parentIdAttribute: 'challengeId',
        associatedItemIdAttribute: 'challengeReferralId'
    },
	constructor: function(){
		this.callParent(arguments);

		this.clear();
		this.getParentItems();
		
		return this;
	}
});