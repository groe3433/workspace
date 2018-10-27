/*
 * Licensed Materials - Property of IBM
 * IBM Call Center for Commerce (5725-P82)
 * (C) Copyright IBM Corp. 2013 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!extn/reviseShipDate/wizards/reviseShipDate/ReviseShipDateWizardUI", "scbase/loader!isccs/utils/WizardUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WizardUtils"], function(
_dojodeclare, _isccsReviseShipDateWizardUI, _isccsWizardUtils, _scBaseUtils, _scScreenUtils, _scWizardUtils) {
    return _dojodeclare("extn.reviseShipDate.wizards.reviseShipDate.ReviseShipDateWizard", [_isccsReviseShipDateWizardUI], {
        // custom code here
        handleWizardCloseConfirmation: function(
        res, args) {
            var argumentList = null;
            var closeTab = false;
            argumentList = _scBaseUtils.getAttributeValue("argumentList", false, args);
            if (!(
            _scBaseUtils.isVoid(
            argumentList))) {
                closeTab = _scBaseUtils.getAttributeValue("closeTab", false, argumentList);
            }
            if (
            _scBaseUtils.or(
            this.customEntityExists(), closeTab) && _scBaseUtils.negateBoolean(
            _scBaseUtils.equals(
            res, "Cancel"))) {
                _scWizardUtils.closeWizard(
                this);
            } else {
                _scScreenUtils.clearScreen(
                this, null);
                _isccsWizardUtils.handleWizardCloseConfirmation(
                this, res, args);
            }
        }
    });
});