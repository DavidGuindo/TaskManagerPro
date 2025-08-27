import { Process } from "./process";

export interface Task {
    id: number | null,
    stateID: number;
    stateName: String;
    authorID: number;
    authorName: String;
    ownerID: number;
    ownerName: String;
    dptID: number;
    dptName: String;
    dateIni: Date | null;
    dateEnd: Date | null;
    description: string;
    processDtos: Process[] | null;

}
