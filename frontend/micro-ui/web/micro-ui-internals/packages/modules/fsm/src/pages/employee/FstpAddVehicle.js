import React, { useEffect, useState } from "react";
import { FormStep, TextInput, LabelFieldPair, CardLabel } from "@egovernments/digit-ui-react-components";
import { useForm, Controller } from "react-hook-form";
import _ from "lodash";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";

const FstpAddVehicle = ({ onSelect }) => {
    const { t } = useTranslation();
    const history = useHistory();
    const [vehicleNumber, setVehicleNumber] = useState("");

    let inputs = {
        "texts": {
            "header": t("ES_FSM_ADD_VEHICLE_LOG"),
            "cardText": "",
            "submitBarLabel": "CS_COMMON_NEXT",
        },
        "inputs": [
            {
                "label": t("ES_FSM_VEHICLE_NUMBER"),
                "type": "text",
                "name": "vehicleNumber",
                "validation": {
                    // "pattern": "[a-zA-Z0-9 ]{1,20}",
                    "pattern": `[A-Z]{2}\\s{1}[0-9]{2}\\s{0,1}[A-Z]{1,2}\\s{1}[0-9]{4}`,
                    "title": "Please use the correct format while adding the Vehicle No.(capital letters and spaces) e.g. AB 00 CD 1234"
                }
            },
        ],
        "key": "vehicleNumber"
    }

    const onSubmit = (data) => {
        history.push(`/${window?.contextPath}/employee/fsm/fstp-fsm-request/${data.vehicleNumber.trim()}`)
    }

    function onChange(e) {
        (e.target.value.trim()).length != 0 ? setVehicleNumber(e.target.value) : null;
    }

    return (
        <React.Fragment>
            <FormStep
                config={inputs}
                onChange={onChange}
                onSelect={onSubmit}
                t={t}
                isDisabled={!vehicleNumber}
                cardStyle={{ margin: "10px" }}
                textInputStyle={{ maxWidth: "540px" }}
            ></FormStep>
        </React.Fragment>
    );
};

export default FstpAddVehicle;