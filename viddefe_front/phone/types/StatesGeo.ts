export interface States {
    id: Number;
    name: string;
}

export interface Cities {
    id: Number;
    name: string;
    stateId: BigInt;
}